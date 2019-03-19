package top.yangxf.interest.flow.task;

import lombok.Data;
import lombok.EqualsAndHashCode;
import top.yangxf.interest.flow.scan.MethodInfo;
import top.yangxf.interest.flow.task.annotation.VariableScope;

/**
 * @author yangxf
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TaskMethodInfo extends MethodInfo<TaskParameterInfo> {
    private static final long serialVersionUID = -7663583437725283570L;

    private String returnName;
    private VariableScope scope;
    private boolean override;

}
