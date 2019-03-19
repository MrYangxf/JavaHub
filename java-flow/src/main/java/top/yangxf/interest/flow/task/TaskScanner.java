package top.yangxf.interest.flow.task;

import top.yangxf.interest.flow.scan.AbstractScanner;
import top.yangxf.interest.flow.task.annotation.Task;
import top.yangxf.interest.flow.task.annotation.TaskEntry;
import top.yangxf.interest.flow.task.annotation.TaskParam;
import top.yangxf.interest.util.common.ReflectUtil;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static top.yangxf.interest.util.common.ObjectUtil.*;

/**
 * @author yangxf
 */
public class TaskScanner extends AbstractScanner<TaskClassInfo, TaskScanner> {

    @Override
    protected boolean matched(Class<?> cls) {
        return cls.isAnnotationPresent(Task.class) &&
               !ReflectUtil.isAbstract(cls);
    }

    @Override
    protected TaskClassInfo newClassInfo(Class<?> cls) {
        TaskClassInfo classInfo = new TaskClassInfo();
        classInfo.setName(cls.getName());
        classInfo.setCls(cls);
        Task taskAnno = cls.getAnnotation(Task.class);
        if (isNull(taskAnno)) {
            classInfo.setAlias(cls.getSimpleName());
            return classInfo;
        }
        classInfo.setAlias(isEmpty(taskAnno.value()) ? cls.getSimpleName() : taskAnno.value());
        classInfo.setOrder(taskAnno.order());

        List<TaskMethodInfo> methodInfos = new ArrayList<>();

        Stream.of(cls.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(TaskEntry.class))
                .sorted(Comparator.comparingInt(m -> m.getAnnotation(TaskEntry.class).order()))
                .forEach(m -> {
                    TaskMethodInfo methodInfo = new TaskMethodInfo();
                    methodInfos.add(methodInfo);
                    TaskEntry tea = m.getAnnotation(TaskEntry.class);
                    methodInfo.setParent(classInfo);
                    methodInfo.setMethod(m);
                    methodInfo.setName(m.getName());
                    methodInfo.setReturnType(m.getReturnType());
                    methodInfo.setReturnName(tea.returnName());
                    methodInfo.setOrder(tea.order());
                    methodInfo.setScope(tea.scope());
                    methodInfo.setOverride(tea.override());
                    List<TaskParameterInfo> parameterInfos = new ArrayList<>();
                    Parameter[] parameters = m.getParameters();
                    for (int i = 0; i < parameters.length; i++) {
                        TaskParameterInfo parameterInfo = new TaskParameterInfo();
                        parameterInfos.add(parameterInfo);
                        Parameter parameter = parameters[i];
                        TaskParam tpa = parameter.getAnnotation(TaskParam.class);
                        parameterInfo.setParent(methodInfo);
                        parameterInfo.setType(parameter.getType());
                        parameterInfo.setOrder(i);
                        if (nonNull(tpa)) {
                            parameterInfo.setName(tpa.value());
                        }
                    }
                    
                    methodInfo.setParameters(Collections.unmodifiableList(parameterInfos));
                });

        classInfo.setMethods(Collections.unmodifiableList(methodInfos));
        return classInfo;
    }

    private boolean doFilter(Class<?> cls) {
        Class<?>[] interfaces = cls.getInterfaces();

        for (int i = 0; i < interfaces.length; i++) {

            Class<?> inter = interfaces[i];
            if (inter == TaskNode.class || doFilter(inter))
                return true;
        }

        Class<?> superclass = cls.getSuperclass();
        return nonNull(superclass) && doFilter(cls.getSuperclass());

    }

}
