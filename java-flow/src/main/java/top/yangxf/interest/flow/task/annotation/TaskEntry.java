package top.yangxf.interest.flow.task.annotation;

import java.lang.annotation.*;

/**
 * @author yangxf
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface TaskEntry {
    
    /**
     * order of the method invoked
     */
    int order();

    /**
     * if non empty, represent return value name of the method,
     * otherwise, return value is void
     */    
    String returnName() default "";

    /**
     * scope of return value, if {@link VariableScope#GLOBAL}, put the value to context
     */
    VariableScope scope() default VariableScope.LOCAL;

    /**
     * if {@link TaskEntry#scope()} is {@link VariableScope#GLOBAL}, 
     * override the value to context
     */
    boolean override() default true;
}
