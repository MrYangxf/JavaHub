package top.yangxf.interest.flow.task;

import top.yangxf.interest.flow.FlowNode;

/**
 * @author yangxf
 */
public interface TaskNode extends FlowNode {
    default int order() {
        return 0;
    }
}
