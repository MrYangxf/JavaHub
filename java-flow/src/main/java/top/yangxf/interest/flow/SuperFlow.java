package top.yangxf.interest.flow;

import top.yangxf.interest.flow.exception.NotSupportedException;
import top.yangxf.interest.flow.logging.SysLogger;
import top.yangxf.interest.flow.logging.SysLoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static top.yangxf.interest.util.common.ObjectUtil.isEmpty;

/**
 * @author yangxf
 */
public abstract class SuperFlow implements Flow {
    private static final long serialVersionUID = -4849036884362811745L;

    private static final AtomicInteger FLOW_ID_COUNTER = new AtomicInteger();

    protected final SysLogger logger = SysLoggerFactory.getLogger(getClass());

    private AtomicBoolean running = new AtomicBoolean();
    private AtomicBoolean closed = new AtomicBoolean();

    protected boolean autoClose;
    protected String name;

    public SuperFlow(String name) {
        this(name, true);
    }

    public SuperFlow(String name, boolean autoClose) {
        this.autoClose = autoClose;
        this.name = isEmpty(name) ? getClass().getSimpleName() + '-' + FLOW_ID_COUNTER.incrementAndGet() : name;
    }

    protected abstract void doStart();

    @Override
    public void start() {
        try {
            if (running.compareAndSet(false, true)) {
                logger.info("---> Flow : {} ", name);
                doStart();
                if (autoClose)
                    close();
            } else if (isClosed()) {
                logger.error("---> Flow {} already closed.", name);
                throw new NotSupportedException("Flow already closed.");
            } else {
                logger.error("---> Flow {} already start.", name);
                throw new NotSupportedException("Flow already start.");
            }
        } catch (Throwable e) {
            try {
                e.printStackTrace();
            } finally {
                if (!(e instanceof NotSupportedException))
                    close();
            }
        }
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public void close() {
        logger.info("---> Flow {} is closed.", name);
        closed.compareAndSet(false, true);
    }

    @Override
    public boolean isClosed() {
        return closed.get();
    }

    protected void setFlow(FlowContext ctx, Flow flow) {
        ctx.setFlow(flow);
    }

    protected void setNode(FlowContext ctx, FlowNode node) {
        ctx.setCurrentNode(node);
    }
}
