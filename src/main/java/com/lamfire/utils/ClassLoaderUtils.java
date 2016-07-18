package com.lamfire.utils;

import java.io.*;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class ClassLoaderUtils {

    public static Iterator<URL> getResources(String resourceName, Class callingClass, boolean aggregate)
                                                                                                        throws IOException {
        AggregateIterator iterator = new AggregateIterator();

        iterator.addEnumeration(Thread.currentThread().getContextClassLoader().getResources(resourceName));

        if ((!iterator.hasNext()) || (aggregate)) {
            iterator.addEnumeration(ClassLoaderUtils.class.getClassLoader().getResources(resourceName));
        }

        if ((!iterator.hasNext()) || (aggregate)) {
            ClassLoader cl = callingClass.getClassLoader();

            if (cl != null) {
                iterator.addEnumeration(cl.getResources(resourceName));
            }
        }

        if ((!iterator.hasNext()) && (resourceName != null)
            && ((resourceName.length() == 0) || (resourceName.charAt(0) != '/'))) {
            return getResources('/' + resourceName, callingClass, aggregate);
        }

        return iterator;
    }

    public static URL getResource(String resourceName, Class callingClass) {
        URL url = Thread.currentThread().getContextClassLoader().getResource(resourceName);

        if (url == null) {
            url = ClassLoaderUtils.class.getClassLoader().getResource(resourceName);
        }

        if (url == null) {
            ClassLoader cl = callingClass.getClassLoader();

            if (cl != null) {
                url = cl.getResource(resourceName);
            }
        }

        if ((url == null) && (resourceName != null)
            && ((resourceName.length() == 0) || (resourceName.charAt(0) != '/'))) {
            return getResource('/' + resourceName, callingClass);
        }

        return url;
    }

    public static InputStream getResourceAsStream(String resourceName, Class callingClass) {
        URL url = getResource(resourceName, callingClass);
        try {
            return url != null ? url.openStream() : null;
        } catch (IOException e) {
        }
        return null;
    }

    public static File getResourceAsFile(String resourceName, Class callingClass) {
        URL url = getResource(resourceName, callingClass);
        return new File(url.getFile());
    }

    public static Class loadClass(String className, Class callingClass) throws ClassNotFoundException {
        try {
            return Thread.currentThread().getContextClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException ex) {
                try {
                    return ClassLoaderUtils.class.getClassLoader().loadClass(className);
                } catch (ClassNotFoundException exc) {
                }
            }
        }
        return callingClass.getClassLoader().loadClass(className);
    }

    public static Class loadClass(String className) throws ClassNotFoundException {
        return loadClass(className, ClassLoaderUtils.class);
    }

    static class AggregateIterator<E> implements Iterator<E> {

        LinkedList<Enumeration<E>> enums  = new LinkedList();
        Enumeration<E>             cur    = null;
        E                          next   = null;
        Set<E>                     loaded = new HashSet();

        public AggregateIterator<E> addEnumeration(Enumeration<E> e) {
            if (e.hasMoreElements()) {
                if (this.cur == null) {
                    this.cur = e;
                    this.next = e.nextElement();
                    this.loaded.add(this.next);
                } else {
                    this.enums.add(e);
                }
            }
            return this;
        }

        public boolean hasNext() {
            return this.next != null;
        }

        public E next() {
            if (this.next != null) {
                E prev = this.next;
                this.next = loadNext();
                return prev;
            }
            throw new NoSuchElementException();
        }

        private Enumeration<E> determineCurrentEnumeration() {
            if ((this.cur != null) && (!this.cur.hasMoreElements())) {
                if (this.enums.size() > 0) this.cur = ((Enumeration) this.enums.removeLast());
                else {
                    this.cur = null;
                }
            }
            return this.cur;
        }

        private E loadNext() {
            if (determineCurrentEnumeration() != null) {
                E tmp = this.cur.nextElement();
                int loadedSize = this.loaded.size();
                while (this.loaded.contains(tmp)) {
                    tmp = loadNext();
                    if (tmp == null) break;
                    if (this.loaded.size() > loadedSize) {
                        break;
                    }
                }
                if (tmp != null) {
                    this.loaded.add(tmp);
                }
                return tmp;
            }
            return null;
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    public static ClassLoader getStandardClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    public static ClassLoader getFallbackClassLoader() {
        return ClassLoaderUtils.class.getClassLoader();
    }

    public static Object createNewInstance(String className) throws ClassNotFoundException, IllegalAccessException,
                                                            InstantiationException {
        Class clazz;
        try {
            clazz = Class.forName(className, true, getStandardClassLoader());
        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName(className, true, getFallbackClassLoader());
            } catch (ClassNotFoundException ex) {
                throw new ClassNotFoundException("Unable to load class " + className + ". Initial cause was "
                                                 + e.getMessage(), e);
            }
        }
        Object newInstance;
        try {
            newInstance = clazz.newInstance();
        } catch (IllegalAccessException e) {
            throw e;
        } catch (InstantiationException e) {
            throw e;
        }

        return newInstance;
    }

    public static Set<Class<?>> getClasses(final String packageName) throws IOException, ClassNotFoundException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        return getClasses(loader, packageName);
    }

    public static Set<Class<?>> getClasses(final ClassLoader loader, final String packageName) throws IOException,
                                                                                              ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = loader.getResources(path);
        if (resources != null) {
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                String protocol = url.getProtocol();

                if (StringUtils.equals("file", protocol)) {
                    String filePath = URLUtils.decode(url.getFile(), "utf-8");
                    classes.addAll(getClassesFromDirectory(new File(filePath), packageName));
                } else if (StringUtils.equalsIgnoreCase("jar", protocol)) {
                    JarFile jar = ((JarURLConnection) url.openConnection()).getJarFile();
                    classes.addAll(getClassesFromJARFile(jar, path));
                }
            }
        }
        return classes;
    }

    private static String stripFilenameExtension(final String filename) {
        if (filename.indexOf('.') != -1) {
            return filename.substring(0, filename.lastIndexOf('.'));
        }
        return filename;

    }

    public static Set<Class<?>> getClassesFromDirectory(final File directory, final String packageName)
                                                                                                       throws ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                String subPackageName = packageName + "." + file.getName();
                classes.addAll(getClassesFromDirectory(file, subPackageName));
            } else {
                String fileName = file.getName();
                if (fileName.endsWith(".class")) {
                    String name = packageName + '.' + stripFilenameExtension(fileName);
                    Class<?> clazz = loadClass(name);
                    classes.add(clazz);
                }
            }
        }
        return classes;
    }

    public static Set<Class<?>> getClassesFromJARFile(final File jarFile, final String packageName)
                                                                                                   throws IOException,
                                                                                                   FileNotFoundException,
                                                                                                   ClassNotFoundException {
        return getClassesFromJARFile(new JarFile(jarFile), packageName);
    }

    public static Set<Class<?>> getClassesFromJARFile(final String jarFile, final String packageName)
                                                                                                     throws IOException,
                                                                                                     FileNotFoundException,
                                                                                                     ClassNotFoundException {
        return getClassesFromJARFile(new JarFile(jarFile), packageName);
    }

    public static Set<Class<?>> getClassesFromJARFile(final JarFile jarFile, final String packageName)
                                                                                                      throws IOException,
                                                                                                      FileNotFoundException,
                                                                                                      ClassNotFoundException {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            if (jarEntry != null) {
                String className = jarEntry.getName();
                if (className.endsWith(".class")) {
                    className = stripFilenameExtension(className);
                    if (className.startsWith(packageName)) {
                        classes.add(loadClass(className.replace('/', '.')));
                    }
                }
            }
        }
        return classes;
    }
}
