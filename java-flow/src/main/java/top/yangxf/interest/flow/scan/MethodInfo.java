package top.yangxf.interest.flow.scan;

import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author yangxf
 */
@Data
public class MethodInfo<P extends ParameterInfo> implements Serializable {
    private static final long serialVersionUID = 2938225768116058218L;

    private ClassInfo parent;
    private Method method;
    private String name;
    private int order;
    private Class<?> returnType;
    
    private List<P> parameters;
}
