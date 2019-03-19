package top.yangxf.interest.flow.scan;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author yangxf
 */
@Data
public class ClassInfo<M extends MethodInfo> implements Serializable {
    private static final long serialVersionUID = 8158996125832138481L;
    private String name;
    private String alias;
    private Class<?> cls;
    
    private List<M> methods;
}
