package top.yangxf.interest.flow.resource;

import static top.yangxf.interest.util.common.ReflectUtil.packageToPath;
import static top.yangxf.interest.util.common.ReflectUtil.pathToPackage;

/**
 * @author yangxf
 */
public class ClassNameLoader extends AbstractResourceLoader {

    private String packageName;

    public ClassNameLoader(String packageName) {
        this.packageName = packageName;
    }

    @Override
    protected String getRootPath() {
        return packageToPath(packageName);
    }

    @Override
    protected String pathToName(String relativePath) {
        return pathToPackage(relativePath).substring(0, relativePath.length() - 6);
    }

    @Override
    protected boolean filter(String resourceName) {
        return resourceName.endsWith(".class");
    }

}
