package top.yangxf.interest.flow.resource;

import java.nio.file.Path;

/**
 * @author yangxf
 */
public interface ResourceLoader {
    
    Iterable<String> load();

    ClassLoader getClassLoader();
    
    Path getClassPath();
}
