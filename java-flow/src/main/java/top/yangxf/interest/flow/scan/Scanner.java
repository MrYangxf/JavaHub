package top.yangxf.interest.flow.scan;

import java.util.Map;

/**
 * @author yangxf
 */
public interface Scanner<V extends ClassInfo, R extends Scanner> {

    R scan(String rootPath);

    R scan(String[] rootPaths);

    Map<String, V> result();
}
