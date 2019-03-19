package top.yangxf.interest.flow;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yangxf
 */
@Getter
@Setter(value = AccessLevel.PACKAGE)
@ToString
public class FlowContext implements Serializable {
    private static final long serialVersionUID = -7893831935490849223L;

    private final ConcurrentMap<String, Object> ATTRIBUTES = new ConcurrentHashMap<>();
    
    private Flow flow;
    private FlowNode currentNode;
    
    public FlowContext putAttr(String name, Object attribute) {
        ATTRIBUTES.put(name, attribute);
        return this;
    }

    public FlowContext putAttrIfAbsent(String name, Object attribute) {
        ATTRIBUTES.putIfAbsent(name, attribute);
        return this;
    }

    public FlowContext removeAttr(String name) {
        ATTRIBUTES.remove(name);
        return this;
    }
    
    public void clear() {
        ATTRIBUTES.clear();
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getAttr(String name) {
        return (T) ATTRIBUTES.get(name);
    }

    public static FlowContext newContext() {
        return new FlowContext();
    }
    
}
