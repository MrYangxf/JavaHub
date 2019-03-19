package top.yangxf.interest.util.common;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static top.yangxf.interest.util.common.ObjectUtil.checkNotNull;
import static top.yangxf.interest.util.common.ObjectUtil.isNull;

/**
 * @author yangxf
 */
public class ReflectUtil {

    private static final char DEFAULT_PACKAGE_SEPARATOR = '.';
    private static final char DEFAULT_PATH_SEPARATOR = '/';

    private ReflectUtil() {
        throw new InstantiationError("ReflectUtil can't be instantiated");
    }

    /**
     * Get ClassLoader, give priority to current thread context class ClassLoader
     *
     * @param cls get cls' ClassLoader if the current thread cannot get the context ClassLoader
     * @return the ClassLoader
     */
    public static ClassLoader getClassLoader(Class<?> cls) {
        checkNotNull(cls, " class ");

        ClassLoader loader = null;
        try {
            loader = Thread.currentThread().getContextClassLoader();
        } catch (Exception e) {
            // if the current thread cannot get the context ClassLoader
        }

        return isNull(loader) ? cls.getClassLoader() : loader;
    }

    /**
     * Get default ClassLoader
     *
     * @see ReflectUtil#getClassLoader(Class)
     */
    public static ClassLoader getDefaultClassLoader() {
        return getClassLoader(ReflectUtil.class);
    }


    public static boolean isStatic(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }

    public static boolean isAbstract(Class<?> cls) {
        return Modifier.isAbstract(cls.getModifiers());
    }

    public static Field[] getAllDeclaredFields(Class<?> cls) {

        Field[] selfFields = cls.getDeclaredFields();
        Class<?> superclass = cls.getSuperclass();
        if (isNull(superclass) || superclass == Object.class)
            return selfFields;

        Field[] superFields = getAllDeclaredFields(superclass);
        int selfLen, superLen;
        if ((selfLen = selfFields.length) == 0)
            return superFields;
        if ((superLen = superFields.length) == 0)
            return selfFields;

        Field[] mergeFields = new Field[selfLen + superLen];
        System.arraycopy(selfFields, 0, mergeFields, 0, selfLen);
        System.arraycopy(superFields, 0, mergeFields, selfLen, superLen);
        return mergeFields;
    }

    /**
     * Package name to path name
     *
     * @param packageName name of package
     * @return name of path
     */
    public static String packageToPath(String packageName) {
        return packageName.replace(DEFAULT_PACKAGE_SEPARATOR, DEFAULT_PATH_SEPARATOR);
    }

    /**
     * Path name to package name
     *
     * @param pathName name of path
     * @return name of package
     */
    public static String pathToPackage(String pathName) {
        int lastIdx = pathName.length() - 1;

        if (pathName.indexOf('/') == 0)
            pathName = pathName.substring(1);

        if (pathName.lastIndexOf('/') == lastIdx)
            pathName = pathName.substring(0, lastIdx);

        return pathName.replace(DEFAULT_PATH_SEPARATOR, DEFAULT_PACKAGE_SEPARATOR);
    }

    /**
     * Path name to class name
     * /com/example/Main.class -> com.example.Main
     *
     * @param pathName name of path
     * @return the class name
     */
    public static String pathToClass(String pathName) {
        int pointIdx = pathName.lastIndexOf('.'),
                lastIdx;
        String clsName = pointIdx > -1 ? pathName.substring(0, pointIdx) : pathName;

        if (clsName.indexOf('/') == 0)
            clsName = clsName.substring(1);

        if (clsName.lastIndexOf('/') == (lastIdx = clsName.length() - 1))
            clsName = clsName.substring(0, lastIdx);

        return clsName.replace(DEFAULT_PATH_SEPARATOR, DEFAULT_PACKAGE_SEPARATOR);
    }

    public static Class<?> getWrapOfPrimitive(Class<?> p) {
        if (isNull(p) || !p.isPrimitive())
            return p;

        switch (p.getSimpleName()) {
            case "byte":
                return Byte.class;
            case "short":
                return Short.class;
            case "char":
                return Character.class;
            case "int":
                return Integer.class;
            case "long":
                return Long.class;
            case "float":
                return Float.class;
            case "double":
                return Double.class;
            default:
        }
        return p;
    }
}