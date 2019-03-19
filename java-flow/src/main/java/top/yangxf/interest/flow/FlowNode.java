package top.yangxf.interest.flow;

import java.io.Serializable;

/**
 * @author yangxf
 */
public interface FlowNode extends Serializable {

    void doTask(FlowContext ctx, Invoker invoker);

    default String name() {
        return getClass().getSimpleName() + '-' + Integer.toHexString(hashCode());
    }
}
