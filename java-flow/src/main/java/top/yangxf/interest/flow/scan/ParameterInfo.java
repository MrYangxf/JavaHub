package top.yangxf.interest.flow.scan;

import lombok.Data;

import java.io.Serializable;

/**
 * @author yangxf
 */
@Data
public class ParameterInfo implements Serializable {
    private static final long serialVersionUID = 8471576029176722569L;

    private MethodInfo parent;
    private Class<?> type;
    private String name;
    private int order;
}
