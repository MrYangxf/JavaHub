package top.yangxf.interest.flow.task.annotation;

import java.lang.annotation.*;

/**
 * @author yangxf
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Task {

    /**
     * unique task name
     */
    String value() default "";

    int order() default 0;
    
}
