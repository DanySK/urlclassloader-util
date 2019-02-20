package org.danilopianini.urlclassloader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sun.misc.URLClassPath;
import sun.net.www.protocol.file.FileURLConnection;

/**
 * Utility to manipulate classpath. The implementation relies heavily on
 * reflection, makes access to non-public APIs, and as such must be used only if
 * no better solutions are available. This library may work only on a subset of
 * Java Runtimes. Newer Java runtimes may break its functionality.
 */
public final class URLClassLoaderUtil {

    private URLClassLoaderUtil() {
    }

    /**
     * List all URLS from the provided classloader.
     *
     * @param cl
     *            the target classloader
     */
    public static URL[] list(final ClassLoader cl) {
        try {
            for (Class<?> clazz = Objects.requireNonNull(cl).getClass(); !Object.class.equals(clazz); clazz = clazz.getSuperclass()) {
                for (final Field potentialURLClasspath : clazz.getDeclaredFields()) {
                    if (URLClassPath.class.isAssignableFrom(potentialURLClasspath.getType())) {
                        potentialURLClasspath.setAccessible(true);
                        final URLClassPath urlClasspath = (URLClassPath) potentialURLClasspath.get(cl);
                        return urlClasspath.getURLs();
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }

        return new URL[0];
    }

    /**
     * Adds the provided URL as first entry in the provided classloader.
     * 
     * @param url
     *            the url
     * @param cl
     *            the target classloader
     */
    public static void addFirst(final URL url, final ClassLoader cl) {
        doOn(new OpOnLists() {
            @Override
            protected void doOnList(final List<URL> urlList) {
                urlList.add(0, url);
            }
        }, cl);
    }

    /**
     * Adds the provided URL as first entry in {@link ClassLoader#getSystemClassLoader()}.
     * 
     * @param url
     *            the url
     */
    public static void addFirst(final URL url) {
        addFirst(url, ClassLoader.getSystemClassLoader());
    }

    /**
     * Adds the provided URI as first entry in the provided classloader.
     * 
     * @param uri
     *            the url
     * @param cl
     *            the target classloader
     * @throws IllegalStateException
     *             if the URI can not be translated to a valid URL
     */
    public static void addFirst(final URI uri, final ClassLoader cl) {
        addFirst(toURL(uri), cl);
    }

    /**
     * Adds the provided URI as first entry in {@link ClassLoader#getSystemClassLoader()}.
     * 
     * @param uri
     *            the url
     * @throws IllegalStateException
     *             if the URI can not be translated to a valid URL
     */
    public static void addFirst(final URI uri) {
        addFirst(toURL(uri));
    }

    /**
     * Adds the provided URL as first entry in the provided classloader.
     * 
     * @param url
     *            the url
     * @param cl
     *            the target classloader
     * @throws IllegalStateException
     *             if the String can not be translated to a valid URL
     */
    public static void addFirst(final String url, final ClassLoader cl) {
        addFirst(toURL(url), cl);
    }

    /**
     * Adds the provided URL as first entry in {@link ClassLoader#getSystemClassLoader()}.
     * 
     * @param url
     *            the url
     * @throws IllegalStateException
     *             if the String can not be translated to a valid URL
     */
    public static void addFirst(final String url) {
        addFirst(toURL(url));
    }

    /**
     * Adds the provided File as first entry in the provided classloader.
     * 
     * @param file
     *            the url
     * @param cl
     *            the target classloader
     */
    public static void addFirst(final File file, final ClassLoader cl) {
        addFirst(toURL(file), cl);
    }

    /**
     * Adds the provided File as first entry in {@link ClassLoader#getSystemClassLoader()}.
     * 
     * @param file
     *            the {@link URL}
     */
    public static void addFirst(final File file) {
        addFirst(toURL(file));
    }

    /**
     * Adds the provided URL as last entry in the provided {@link ClassLoader}.
     * 
     * @param url
     *            the {@link URL}
     * @param cl
     *            the {@link ClassLoader}
     */
    public static void addLast(final URL url, final ClassLoader cl) {
        doOn(new Op() {
            @Override
            public void run(final URLClassPath cl) {
                try {
                    final Method add = cl.getClass().getMethod("addURL", URL.class);
                    add.setAccessible(true);
                    add.invoke(cl, url);
                } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    throw new IllegalStateException(e);
                }
            }
        }, cl);
    }

    /**
     * Adds the provided File as last entry in {@link ClassLoader#getSystemClassLoader()}.
     * 
     * @param url
     *            the {@link URL}
     */
    public static void addLast(final URL url) {
        addLast(url, ClassLoader.getSystemClassLoader());
    }

    /**
     * Adds the provided File as last entry in the provided {@link ClassLoader}.
     * 
     * @param uri
     *            the {@link URI}
     * @param cl
     *            the {@link ClassLoader}
     */
    public static void addLast(final URI uri, final ClassLoader cl) {
        addLast(toURL(uri), cl);
    }

    /**
     * Adds the provided File as last entry in {@link ClassLoader#getSystemClassLoader()}.
     * 
     * @param uri
     *            the {@link URI}
     */
    public static void addLast(final URI uri) {
        addLast(toURL(uri));
    }

    /**
     * @param url
     *            the {@link URL}
     * @param cl
     *            the {@link ClassLoader}
     */
    public static void addLast(final String url, final ClassLoader cl) {
        addLast(toURL(url), cl);
    }

    /**
     * @param url
     *            the {@link URL}
     */
    public static void addLast(final String url) {
        addLast(toURL(url));
    }

    /**
     * @param file
     *            the url
     * @param cl
     *            the target classloader
     */
    public static void addLast(final File file, final ClassLoader cl) {
        addLast(toURL(file), cl);
    }

    /**
     * @param file
     *            the url
     */
    public static void addLast(final File file) {
        addLast(toURL(file));
    }

    /**
     * @param url
     *            the {@link URL}
     * @param cl
     *            the {@link ClassLoader}
     */
    public static void remove(final URL url, final ClassLoader cl) {
        doOn(new OpOnLists() {
            @Override
            protected void doOnList(final List<URL> urlList) {
                urlList.remove(url);
            }
        }, cl);
        doOn(new Op() {
            @Override
            public void run(final URLClassPath cl) {
                try {
                    final Field loadersField = cl.getClass().getDeclaredField("loaders");
                    final Field lmapField = cl.getClass().getDeclaredField("lmap");
                    loadersField.setAccessible(true);
                    lmapField.setAccessible(true);
                    final List<?> loaders = (List<?>) loadersField.get(cl);
                    final Map<?, ?> lmap = (Map<?, ?>) lmapField.get(cl);
                    final Iterator<?> it = loaders.iterator();
                    Method target = null;
                    while (it.hasNext()) {
                        final Object loader = it.next();
                        for (Class<?> loaderClass = loader.getClass(); target == null && !Object.class.equals(loaderClass); loaderClass = loaderClass.getSuperclass()) {
                            final Method[] methods = loaderClass.getDeclaredMethods();
                            int i;
                            for (i = 0; i < methods.length; i++) {
                                final Method m = methods[i];
                                if (m.getName().equals("getBaseURL") && m.getParameterTypes().length == 0) {
                                    target = m;
                                    break;
                                }
                            }
                        }
                        if (target == null) {
                            throw new IllegalStateException("Could not find any getBaseURL() method");
                        }
                        target.setAccessible(true);
                        URL clUrl = (URL)target.invoke(loader);
                        if (clUrl.getProtocol().equals("jar")) {
                            URLConnection urlConnection = clUrl.openConnection();
                            if(urlConnection instanceof JarURLConnection) {
                                JarURLConnection jarURLConnection = (JarURLConnection) urlConnection;
                                clUrl = jarURLConnection.getJarFileURL();
                            }
                        }
                        if (clUrl.equals(url)) {
                            it.remove();
                            lmap.values().remove(loader);
                        }
                    }
                } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | IOException e) {
                    throw new IllegalStateException(e);
                }
            }
        }, cl);
    }

    /**
     * @param url
     *            the {@link URL}
     */
    public static void remove(final URL url) {
        remove(url, ClassLoader.getSystemClassLoader());
    }

    /**
     * @param uri
     *            the {@link URI}
     * @param cl
     *            the {@link ClassLoader}
     */
    public static void remove(final URI uri, final ClassLoader cl) {
        remove(toURL(uri), cl);
    }

    /**
     * @param uri
     *            the {@link URI}
     */
    public static void remove(final URI uri) {
        remove(toURL(uri));
    }

    /**
     * @param url
     *            the {@link URL}
     * @param cl
     *            the {@link ClassLoader}
     */
    public static void remove(final String url, final ClassLoader cl) {
        remove(toURL(url), cl);
    }

    /**
     * @param url
     *            the {@link URL}
     */
    public static void remove(final String url) {
        remove(toURL(url));
    }

    /**
     * @param file
     *            the url
     * @param cl
     *            the target classloader
     */
    public static void remove(final File file, final ClassLoader cl) {
        remove(toURL(file), cl);
    }

    /**
     * @param file
     *            the url
     */
    public static void remove(final File file) {
        remove(toURL(file));
    }

    private static void doOn(final Op operation, final ClassLoader loader) {
        try {
            for (Class<?> clazz = Objects.requireNonNull(loader).getClass(); !Object.class.equals(clazz); clazz = clazz.getSuperclass()) {
                for (final Field potentialURLClasspath : clazz.getDeclaredFields()) {
                    if (URLClassPath.class.isAssignableFrom(potentialURLClasspath.getType())) {
                        potentialURLClasspath.setAccessible(true);
                        final URLClassPath urlClasspath = (URLClassPath) potentialURLClasspath.get(loader);
                        operation.run(urlClasspath);
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }

    private static URL toURL(final URI uri) {
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
    }

    private static URL toURL(final String s) {
        try {
            return toURL(URI.create(s));
        } catch (IllegalArgumentException e) {
            return toURL(new File(s));
        }
    }

    private static URL toURL(final File file) {
        return toURL(file.toURI());
    }

    private abstract static class OpOnLists extends Op {
        @Override
        public final void run(final URLClassPath cl) {
            for (final Field potentialURLList : cl.getClass().getDeclaredFields()) {
                if (!Modifier.isStatic(potentialURLList.getModifiers()) && List.class.isAssignableFrom(potentialURLList.getType())) {
                    potentialURLList.setAccessible(true);
                    try {
                        final List<?> theList = (List<?>) potentialURLList.get(cl);
                        if (theList != null && (theList.isEmpty() || theList.get(0) instanceof URL)) {
                            /*
                             * This is most likely one of our targets
                             */
                            @SuppressWarnings("unchecked")
                            final List<URL> urlList = (List<URL>) theList;
                            doOnList(urlList);
                        }
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                        throw new IllegalStateException(e);
                    }
                }
            }
        }
        protected abstract void doOnList(List<URL> urlList);
    }

    private abstract static class Op {
        public abstract void run(URLClassPath l);
    }

}