package top.yangxf.interest.flow.task;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.yangxf.interest.flow.scan.ClassInfo;

/**
 * @author yangxf
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TaskClassInfo extends ClassInfo<TaskMethodInfo> {
    private static final long serialVersionUID = -520759865233885989L;

    private int order;
}