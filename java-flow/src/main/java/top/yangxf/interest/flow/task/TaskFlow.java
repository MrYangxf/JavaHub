package top.yangxf.interest.flow.task;

import top.yangxf.interest.flow.FlowContext;
import top.yangxf.interest.flow.FlowNode;
import top.yangxf.interest.flow.Invoker;
import top.yangxf.interest.flow.SuperFlow;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static top.yangxf.interest.util.common.ObjectUtil.*;

/**
 * @author yangxf
 */
public class TaskFlow extends SuperFlow {
    private static final long serialVersionUID = 2190658552883428785L;

    private final FlowNode START = (ctx, invoker) -> {
        logger.info("---> TASK FLOW START ----");
        ctx.putAttr(START_TIME_CTX_VAR_NAME, System.currentTimeMillis());
        ctx.putAttr(LOGGER_CTX_VAR_NAME, logger);
        setFlow(ctx, this);
        invoker.invoke(ctx);
    };
    private final Invoker END = ctx -> {
        logger.info("---> TASK FLOW END   ----");
        logger.info("---> DURATION {} millis", System.currentTimeMillis() - ctx.<Long>getAttr(START_TIME_CTX_VAR_NAME));
    };

    private boolean reorder;
    private volatile Invoker startTask;
    private FlowContext context;
    private LinkedList<TaskNode> taskNodes;

    public TaskFlow() {
        this("");
    }

    public TaskFlow(List<TaskNode> taskNodes) {
        this(null, taskNodes);
    }

    public TaskFlow(String name) {
        this(name, new LinkedList<>());
    }

    public TaskFlow(String name, FlowContext ctx) {
        this(name, ctx, new LinkedList<>(), false, true);
    }

    public TaskFlow(String name, List<TaskNode> taskNodes) {
        this(name, new FlowContext(), taskNodes, false, true);
    }

    public TaskFlow(String name, FlowContext context, List<TaskNode> taskNodes, boolean reorder, boolean autoClose) {
        super(name);
        if (isNull(context))
            throw new NullPointerException("task context is not null");
        if (isNull(taskNodes))
            throw new NullPointerException("task taskNodes is not null");

        this.reorder = reorder;
        this.autoClose = autoClose;
        this.context = context;
        this.taskNodes = taskNodes instanceof LinkedList ?
                (LinkedList<TaskNode>) taskNodes : new LinkedList<>(taskNodes);
    }

    @Override
    protected void doStart() {
        getStartTask().invoke(context);
    }

    public TaskFlow addFlowNode(TaskNode node) {
        checkNotNull(node, "cant't add null task node");
        taskNodes.addLast(node);
        return this;
    }

    public TaskFlow addFlowNode(int index, TaskNode node) {
        checkNotNull(node, "cant't add null task node");
        taskNodes.add(index, node);
        return this;
    }

    public TaskFlow addFlowNode(String prevName, TaskNode node) {
        if (isEmpty(prevName)) {
            logger.warn("task {} is empty, add to last.");
            return addFlowNode(0, node);
        }

        for (int i = 0; i < taskNodes.size(); i++) {
            TaskNode n = taskNodes.get(i);
            if (prevName.equals(n.name())) {
                return addFlowNode(i + 1, node);
            }
        }

        logger.warn("task {} not exists, add to last.", prevName);
        addFlowNode(node);
        return this;
    }

    @Override
    public void close() {
        if (!isClosed()) {
            super.close();
            startTask = null;
            taskNodes = null;
            context.clear();
            context = null;
        }
    }

    private Invoker getStartTask() {
        if (isNull(startTask)) {
            synchronized (TaskFlow.class) {
                if (isNull(startTask)) {
                    startTask = getFlow();
                }
            }
        }

        return startTask;
    }

    private Invoker getFlow() {
        if (isEmpty(taskNodes))
            return ctx -> START.doTask(ctx, END);

        List<TaskNode> sortedNodes = reorder ? taskNodes.stream()
                                                        .sorted(Comparator.comparingInt(TaskNode::order))
                                                        .collect(Collectors.toList()) : taskNodes;

        Invoker last = END;
        for (int i = sortedNodes.size() - 1; i >= 0; i--) {
            TaskNode node = sortedNodes.get(i);
            Invoker next = last;
            String name = node.name();
            last = ctx -> {
                logger.info("---> NODE {} START ", name);
                ctx.putAttr(CURRENT_TIME_CTX_VAR_NAME, System.currentTimeMillis());
                setNode(ctx, node);
                node.doTask(ctx, next);
            };
        }

        Invoker finalLast = last;
        return ctx -> START.doTask(ctx, finalLast);
    }
}
