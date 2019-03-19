package top.yangxf.interest.flow.resource;

import top.yangxf.interest.flow.exception.ResourceLoaderException;
import top.yangxf.interest.flow.logging.SysLogger;
import top.yangxf.interest.flow.logging.SysLoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

import static javafx.fxml.FXMLLoader.getDefaultClassLoader;
import static top.yangxf.interest.util.common.ObjectUtil.isNull;
import static top.yangxf.interest.util.common.ObjectUtil.nonNull;

/**
 * @author yangxf
 */
public abstract class AbstractResourceLoader implements ResourceLoader {

    final SysLogger logger = SysLoggerFactory.getLogger(getClass());
    private static final char PATH_SEPARATOR = '/';
    private static final String JAR_SEPARATOR = "!/";

    private static final String JAR_PREFIX = "jar:";
    private static final String FILE_PREFIX = "file:";
    private static final int FILE_PREFIX_LEN = FILE_PREFIX.length();

    private ClassLoader classLoader;
    private Path classPath = Paths.get("");
    private String relativePathInJar = "";
    private List<String> resources = new LinkedList<>();

    protected AbstractResourceLoader() {
        this(getDefaultClassLoader());
    }

    protected AbstractResourceLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
        URL url = classLoader.getResource("");
        if (nonNull(url))
            classPath = Paths.get(clipPrefix(url.getPath()));

        String cp = classPath.toString();
        int idx = cp.indexOf('!');
        if (idx > 0) {
            String path = cp.substring(idx + 1);
            if (path.charAt(0) == '/')
                path = path.substring(1);

            int lastIdx = path.length() - 1;
            if (path.charAt(lastIdx) == '!')
                path = path.substring(0, lastIdx);

            lastIdx = path.length() - 1;
            if (lastIdx > -1 && path.charAt(lastIdx) != '/')
                path += PATH_SEPARATOR;

            relativePathInJar = path;
        }

        logger.debug("classpath : {}", classPath);
        logger.debug("relativePathInJar : {}", relativePathInJar);
    }

    /**
     * relative path of resource
     */
    protected abstract String getRootPath();

    /**
     * convert relative path to resource name
     */
    protected abstract String pathToName(String relativePath);

    /**
     * filter by resource name
     */
    protected abstract boolean filter(String resourceName);

    @Override
    public final Iterable<String> load() {
        String rootPath = getRootPath();
        logger.info("load start. root path : {}", rootPath);
        try {
            Enumeration<URL> urls = classLoader.getResources(rootPath);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if (isNull(url))
                    continue;
                String urlPath = url.getPath(),
                        protocol = url.getProtocol();
                logger.info("path : {}", urlPath);
                logger.info("protocol : {}", protocol);
                if ("file".equals(protocol)) {
                    Path path = Paths.get(url.toURI());
                    if (Files.isDirectory(path)) {
                        parseDirectory(path);
                    } else {
                        throw new IllegalArgumentException(String.format("%s is not a directory", path));
                    }
                } else if ("jar".equals(protocol)) {
                    int i = urlPath.indexOf('!');
                    String jarPath = urlPath.substring(FILE_PREFIX_LEN, i);
                    parseJar(new JarFile(jarPath));
                }
            }
        } catch (Exception e) {
            throw new ResourceLoaderException("resource load fail. ", e);
        }
        return resources;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public Path getClassPath() {
        return classPath;
    }

    private String clipPrefix(String path) {
        if (path.startsWith(JAR_PREFIX))
            path = path.substring(JAR_PREFIX.length());

        if (path.startsWith(FILE_PREFIX))
            path = path.substring(FILE_PREFIX_LEN);

        return path;
    }

    private String getResourceName(Path path) {
        return pathToName(classPath.relativize(path).toString());
    }

    private void parseDirectory(Path path) {
        try {
            Files.list(path).forEach(p -> {
                if (Files.isDirectory(p)) {
                    logger.debug("loading dir : {}", p);
                    parseDirectory(p);
                } else {
                    if (filter(p.toString())) {
                        String resourceName = getResourceName(p);
                        logger.debug("append resource : {}", resourceName);
                        resources.add(resourceName);
                    }
                }
            });
        } catch (IOException e) {
            throw new ResourceLoaderException(String.format("load fail, path=%s", path), e);
        }
    }

    private void parseJar(JarFile jarFile) throws IOException {
        String root = jarFile.getName();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            parseJarEntry(root, jarEntry);
        }
    }

    private void parseJarEntry(String root, JarEntry jarEntry) throws IOException {
        if (jarEntry == null)
            return;

        String name = jarEntry.getName();
        logger.debug("name = " + name);
        if (name.endsWith(".jar")) {
            String jarPath = JAR_PREFIX + FILE_PREFIX + PATH_SEPARATOR + root + JAR_SEPARATOR + name;
            logger.debug("loading jar : {}", jarPath);
            URL url = new URL(jarPath);
            try (InputStream inputStream = url.openStream();
                 JarInputStream jis = new JarInputStream(inputStream)) {

                JarEntry je;
                while (nonNull(je = jis.getNextJarEntry())) {
                    parseJarEntry(jarPath + JAR_SEPARATOR, je);
                }
            }
        } else {
            boolean isPrev = name.startsWith(getRootPath()),
                    isMultiPrev = name.startsWith(prefixInJar());
            if (isPrev || isMultiPrev) {
                String simpleName = isPrev ? name : name.substring(relativePathInJar.length());
                if (filter(simpleName)) {
                    String resourceName = pathToName(simpleName);
                    logger.debug("append resource : {}", resourceName);
                    resources.add(resourceName);
                }
            }
        }
    }

    private String prefixInJar() {
        return relativePathInJar + getRootPath();
    }

}
