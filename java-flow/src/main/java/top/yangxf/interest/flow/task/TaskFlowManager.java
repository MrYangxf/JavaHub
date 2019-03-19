package top.yangxf.interest.flow.task;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import top.yangxf.interest.flow.Flow;
import top.yangxf.interest.flow.FlowContext;
import top.yangxf.interest.flow.Invoker;
import top.yangxf.interest.flow.exception.DuplicationVariableException;
import top.yangxf.interest.flow.logging.SysLogger;
import top.yangxf.interest.flow.logging.SysLoggerFactory;
import top.yangxf.interest.flow.resource.ResourceLoader;
import top.yangxf.interest.flow.task.annotation.Task;
import top.yangxf.interest.flow.task.annotation.VariableScope;
import top.yangxf.interest.util.common.ObjectUtil;
import top.yangxf.interest.util.common.ReflectUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import static java.lang.Class.forName;
import static top.yangxf.interest.util.common.ObjectUtil.*;

/**
 * @author yangxf
 */
public class TaskFlowManager {

    private static final SysLogger logger = SysLoggerFactory.getLogger(TaskFlowManager.class);

    private static int VAR_COUNTER = 0;
    private static volatile TaskFlowManager INSTANCE;

    private Map<String, TaskClassInfo> TASKS;
    private Map<String, TaskNode> TASK_NODES = new ConcurrentHashMap<>();

    private TaskFlowManager() {
        init();
    }

    public static TaskFlowManager getInstance() {
        if (isNull(INSTANCE)) {
            synchronized (TaskFlowManager.class) {
                if (isNull(INSTANCE)) {
                    INSTANCE = new TaskFlowManager();
                }
            }
        }

        return INSTANCE;
    }

    public static TaskNode getTaskNode(String alias) {
        return getInstance().TASK_NODES.get(alias);
    }

    public Flow build(FlowContext ctx, TaskName... taskNames) {
        return build(null, ctx, taskNames);
    }

    public Flow build(FlowContext ctx, String... taskClassNames) {
        return build(null, ctx, taskClassNames);
    }

    public Flow build(FlowContext ctx, Class<?>... taskClasses) {
        return build(null, ctx, taskClasses);
    }

    public Flow build(String flowName, FlowContext ctx, String... taskClassNames) {
        checkNotNull((Object[]) taskClassNames);

        Class<?>[] classes = Stream.of(taskClassNames)
                                   .map(clsName -> {
                                       try {
                                           return forName(clsName);
                                       } catch (ClassNotFoundException e) {
                                           logger.error("class {} not found, skip", clsName);
                                           return null;
                                       }
                                   })
                                   .filter(ObjectUtil::nonNull)
                                   .toArray(Class<?>[]::new);


        return build(flowName, ctx, classes);
    }

    public Flow build(String flowName, FlowContext ctx, Class<?>... taskClasses) {
        checkNotNull((Object[]) taskClasses);

        TaskName[] taskNames = Stream.of(taskClasses)
                                     .filter(cls -> cls.isAnnotationPresent(Task.class))
                                     .map(cls -> {
                                         Task taskAnno = cls.getAnnotation(Task.class);
                                         return isEmpty(taskAnno.value()) ? cls.getSimpleName() : taskAnno.value();
                                     })
                                     .map(TaskFlowManager::named).toArray(TaskName[]::new);
        return build(flowName, ctx, taskNames);
    }

    public Flow build(String flowName, FlowContext ctx, TaskName... taskNames) {
        TaskFlow flow = new TaskFlow(flowName, ctx);
        if (isEmpty(taskNames))
            return flow;

        for (int i = 0; i < taskNames.length; i++) {
            TaskName taskName = taskNames[i];
            if (isNull(taskName)) {
                continue;
            }

            String name = taskName.name;
            if (isEmpty(name)) {
                continue;
            }

            TaskNode taskNode = TASK_NODES.get(name);
            if (isNull(taskNode)) {
                logger.warn("task {} not exists, skip.", name);
                continue;
            }
            logger.debug("add task({}) to flow({})", name, flow.name());
            flow.addFlowNode(taskNode);
        }

        return flow;
    }

    /**
     * make class that implements {@link TaskNode}
     */
    private void init() {
        String packageName = System.getProperty("task.scanBasePackage", defaultPackage());
        TaskScanner scanner = new TaskScanner();
        TASKS = scanner.scan(packageName).result();
        ResourceLoader resourceLoader = scanner.getResourceLoader();
        ClassPool pool = new ClassPool(true);
        try {
            pool.appendClassPath(new LoaderClassPath(resourceLoader.getClassLoader()));
            CtClass ctTaskNode = pool.get(TaskNode.class.getName());
            for (Map.Entry<String, TaskClassInfo> entry : TASKS.entrySet()) {
                String alias = entry.getKey();
                TaskClassInfo info = entry.getValue();
                CtClass ctClass = pool.makeClass("FLOW_TASK_" + alias);
                ctClass.addInterface(ctTaskNode);

                String doTaskMethodText = doTaskMethodText(info);
                CtMethod ctDoTask = CtMethod.make(doTaskMethodText, ctClass);
                ctClass.addMethod(ctDoTask);

                String orderMethodText = orderMethodText(info);
                CtMethod ctOrder = CtMethod.make(orderMethodText, ctClass);
                ctClass.addMethod(ctOrder);

                String nameMethodText = nameMethodText(info);
                CtMethod ctName = CtMethod.make(nameMethodText, ctClass);
                ctClass.addMethod(ctName);

                Class cls = ctClass.toClass();
                TaskNode node = (TaskNode) cls.newInstance();
                TASK_NODES.putIfAbsent(alias, node);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String defaultPackage() {
        String defaultPackage = "";
        try {
            Throwable throwable = new Throwable();
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            StackTraceElement bottom = null;
            for (int i = 0; i < stackTrace.length; i++) {
                StackTraceElement ele = stackTrace[i];
                String methodName = ele.getMethodName();
                if ("main".equals(methodName)) {
                    bottom = ele;
                    break;
                }
            }
            if (isNull(bottom)) {
                bottom = stackTrace[stackTrace.length - 1];
            }
            String mainClass = bottom.getClassName();
            Package pkg = forName(mainClass).getPackage();
            defaultPackage = pkg.getName();
        } catch (Throwable e) {
            // nothing
            logger.error("find main class fail : ", e);
        }
        return defaultPackage;
    }

    /**
     * generate doTask method text
     */
    private static String doTaskMethodText(TaskClassInfo info) {
        StringBuilder text = new StringBuilder();
        String ctx = "ctx";
        String nextInvoker = "nextInvoker";
        text.append("public void doTask(")
            .append(FlowContext.class.getName())
            .append(" ").append(ctx).append(", ")
            .append(Invoker.class.getName()).append(" ").append(nextInvoker).append(") {\n");

        Class<?> cls = info.getCls();
        String clsName = cls.getName();
        if (cls.isMemberClass()) {
            clsName = clsName.replaceFirst("\\$", ".");
        }

        String classVarName = varName();

        text.append("   ")
            .append(clsName)
            .append(" ")
            .append(classVarName)
            .append(" = new ")
            .append(clsName)
            .append("();\n");

        // local variable names table
        List<String> localVariables = new ArrayList<>();
        localVariables.add(ctx);
        localVariables.add(nextInvoker);

        List<TaskMethodInfo> methodInfos = info.getMethods();
        for (TaskMethodInfo methodInfo : methodInfos) {
            List<TaskParameterInfo> parameterInfos = methodInfo.getParameters();

            StringBuilder paramVarNames = new StringBuilder();
            for (int i = 0; i < parameterInfos.size(); i++) {
                if (i != 0) {
                    paramVarNames.append(", ");
                }

                TaskParameterInfo parameterInfo = parameterInfos.get(i);
                Class<?> paramType = parameterInfo.getType();
                String paramTypeName = paramType.getName(),
                        paramName = parameterInfo.getName();

                // default parameters "ctx" and "nextInvoker"
                if (ctx.equals(paramName) && paramType != FlowContext.class ||
                    nextInvoker.equals(paramName) && paramType != Invoker.class) {
                    throw new IllegalArgumentException("parameter " + paramName + " must be " + paramTypeName);
                }

                if (paramType == FlowContext.class) {
                    if (isEmpty(paramName)) {
                        paramName = ctx;
                    } else {
                        text.append("   ").append(paramTypeName).append(" ").append(paramName)
                            .append(" = ").append(ctx).append(";\n");
                        localVariables.add(paramName);
                    }
                } else if (paramType == Invoker.class) {
                    if (isEmpty(paramName)) {
                        paramName = nextInvoker;
                    } else {
                        text.append("   ").append(paramTypeName).append(" ").append(paramName)
                            .append(" = ").append(nextInvoker).append(";\n");
                        localVariables.add(paramName);
                    }
                }

                boolean isPrimitive = false;
                String primitiveName = paramTypeName;
                if (paramType.isMemberClass()) {
                    paramTypeName = paramTypeName.replaceFirst("\\$", ".");
                } else if (isPrimitive = paramType.isPrimitive()) {
                    Class<?> wrapOfPrimitive = ReflectUtil.getWrapOfPrimitive(paramType);
                    paramTypeName = wrapOfPrimitive.getSimpleName();
                } else if (paramType.isArray()) {
                    paramTypeName = paramType.getSimpleName();
                }


                // default: get from context if local variable table is not contains the parameter
                if (!localVariables.contains(paramName)) {
                    text.append("   ").append(paramTypeName).append(" ").append(paramName)
                        .append(" = (").append(paramTypeName).append(") ")
                        .append(ctx)
                        .append(".getAttr(\"").append(paramName).append("\");\n");

                    // if null
                    text.append("   if(").append(paramName)
                        .append(" == null) \n       throw new NullPointerException(\"current context is not contains variable ")
                        .append(paramName).append("\");\n");

                    addLocalVariable(localVariables, paramName);
                    // cast to primitive value 
                    if (isPrimitive) {
                        paramName = paramName + "." + primitiveName + "Value()";
                    }
                }

                paramVarNames.append(paramName);
            }

            String returnName = methodInfo.getReturnName();
            text.append("   ");
            if (nonEmpty(returnName)) {
                text.append(methodInfo.getReturnType().getName());
                text.append(" ").append(returnName).append(" = ");
            }
            text.append(classVarName)
                .append(".")
                .append(methodInfo.getName())
                .append("(")
                .append(paramVarNames.toString())
                .append(");\n");

            if (nonEmpty(returnName)) {
                addLocalVariable(localVariables, returnName);
                if (methodInfo.getScope() == VariableScope.GLOBAL) {
                    text.append("   ").append(ctx).append(".");
                    if (methodInfo.isOverride()) {
                        text.append("putAttr(\"");
                    } else {
                        text.append("putAttrIfAbsent(\"");
                    }
                    text.append(returnName).append("\", ")
                        .append(returnName).append(");\n");
                }
            }
        }

        text.append("   Long ").append(Flow.CURRENT_TIME_CTX_VAR_NAME).append(" = (Long) ")
            .append(ctx).append(".getAttr(\"").append(Flow.CURRENT_TIME_CTX_VAR_NAME).append("\");\n");
        text.append("   ((").append(SysLogger.class.getName()).append(")")
            .append(ctx).append(".getAttr(\"")
            .append(Flow.LOGGER_CTX_VAR_NAME)
            .append("\")).info(\"---> NODE {} END    DURATION {} millis\", name(), String.valueOf(System.currentTimeMillis() - ")
            .append(Flow.CURRENT_TIME_CTX_VAR_NAME)
            .append(".longValue()));\n");
        // next.invoke, make sure invoke next task
        text.append("   ").append(nextInvoker).append(".invoke(").append(ctx).append(");\n");
        text.append("}");
        logger.debug("METHOD : \n{}", text.toString());
        return text.toString();
    }

    private static void addLocalVariable(List<String> localVariables, String varName) {
        if (localVariables.contains(varName)) {
            throw new DuplicationVariableException("duplication local variable : " + varName);
        }
        localVariables.add(varName);
    }

    private static String varName() {
        return "arg" + VAR_COUNTER++;
    }

    /**
     * generate order() method text
     */
    private static String orderMethodText(TaskClassInfo info) {
        StringBuilder text = new StringBuilder();
        text.append("public int order() {\n")
            .append("   return ")
            .append(info.getOrder())
            .append(";\n}");
        logger.debug("METHOD : \n{}", text.toString());
        return text.toString();
    }

    /**
     * generate name() method text
     */
    private static String nameMethodText(TaskClassInfo info) {
        StringBuilder text = new StringBuilder();
        text.append("public String name() {\n")
            .append("   return \"")
            .append(info.getAlias())
            .append("\";\n}");
        logger.debug("METHOD : \n{}", text.toString());
        return text.toString();
    }

    public static TaskName named(String name) {
        return new TaskName(name);
    }

    public static final class TaskName {
        private String name;

        private TaskName(String name) {
            this.name = name;
        }
    }

}
