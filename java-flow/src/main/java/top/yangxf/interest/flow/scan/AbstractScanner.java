package top.yangxf.interest.flow.scan;

import top.yangxf.interest.flow.logging.SysLogger;
import top.yangxf.interest.flow.logging.SysLoggerFactory;
import top.yangxf.interest.flow.resource.ClassNameLoader;
import top.yangxf.interest.flow.resource.ResourceLoader;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static top.yangxf.interest.util.common.ObjectUtil.isEmpty;
import static top.yangxf.interest.util.common.ObjectUtil.isNull;

/**
 * scan matched class info by package name.
 * 
 * tips:
 *  - this is a thread unsafe class
 *  
 * @author yangxf
 */
public abstract class AbstractScanner<V extends ClassInfo, R extends Scanner<V, R>> implements Scanner<V, R> {
    final SysLogger logger = SysLoggerFactory.getLogger(getClass());

    protected ConcurrentMap<String, V> classes = new ConcurrentHashMap<>();
    protected AbstractScanner<V, R> scanner;

    private ResourceLoader resourceLoader;

    public AbstractScanner() {
    }

    public AbstractScanner(AbstractScanner<V, R> scanner) {
        this.scanner = scanner;
    }

    protected abstract boolean matched(Class<?> cls);

    protected abstract V newClassInfo(Class<?> cls);

    boolean isMatched(Class<?> cls) {
        return matched(cls) && (scanner == null || scanner.matched(cls));
    }

    @Override
    public Map<String, V> result() {
        return Collections.unmodifiableMap(classes);
    }

    @Override
    public final R scan(String[] rootPaths) {
        if (isEmpty(rootPaths))
            return _self();

        for (int i = 0; i < rootPaths.length; i++)
            scan(rootPaths[i]);

        return _self();
    }

    @Override
    public final R scan(String rootPath) {
        logger.debug("scan path : {}", rootPath);
        if (isNull(rootPath))
            return _self();

        resourceLoader = new ClassNameLoader(rootPath);
        ClassLoader classLoader = resourceLoader.getClassLoader();
        Iterable<String> clsNames = resourceLoader.load();
        for (String currentClsName : clsNames) {
            try {
                Class<?> currentCls = classLoader.loadClass(currentClsName);
                V classInfo = newClassInfo(currentCls);
                String alias = classInfo.getAlias();
                if (classExists(alias)) {
                    logger.warn("alias {} already exist, skip", alias);
                    continue;
                }

                if (isMatched(currentCls))
                    classes.putIfAbsent(alias, classInfo);
            } catch (ClassNotFoundException e) {
                logger.error("class {} not found", currentClsName);
                e.printStackTrace();
            }
        }

        return _self();
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }

    protected boolean classExists(String key) {
        return classes.containsKey(key);
    }

    @SuppressWarnings("unchecked")
    protected R _self() {
        return (R) this;
    }

}
