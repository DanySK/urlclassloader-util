# URLClassLoader util

Utilities for manipulating `sun.misc.URLClassLoader`s

## Status

### Stable branch

[![Build Status](https://travis-ci.org/DanySK/urlclassloader-util.svg?branch=master)](https://travis-ci.org/DanySK/urlclassloader-util)

### Development branch

[![Build Status](https://travis-ci.org/DanySK/urlclassloader-util.svg?branch=develop)](https://travis-ci.org/DanySK/urlclassloader-util)

## Usage

You can add and remove entries for the classloader of your preference. Entries must be `java.net.URL`s (or `java.net.URI`s, or `java.io.File`s, or `java.lang.String`s that can be transformed to `URL`s). The `ClassLoader` must have a field of type `URLClassPath`, that is what this library actually manipulates.

Please be wary that, despite being designed with some degree of reliability in mind, this library accesses non-public APIs, and as such may work only on a subset of Java Runtime Environments, and newer versions of working JREs may break it.
The library is tested against multiple JREs, please refer to the Travis CI build to verify that the JRE you are interested in is currently supported.

### Example with system ClassLoader
```java
// Also URL, URI, or File are ok
final String myDir = ...;
URLClassLoaderUtil.addFirst(myDir);
URLClassLoaderUtil.remove(myDir);
URLClassLoaderUtil.addLast(myDir);
```

### Example with the current class ClassLoader
```java
final ClassLoader cl = MyClass.class.getClassLoader();
// Also URL, URI, or File are ok
final String myDir = ...;
URLClassLoaderUtil.addFirst(myDir, cl);
URLClassLoaderUtil.remove(myDir, cl);
URLClassLoaderUtil.addLast(myDir, cl);
```

### Import URLClassLoader util in your project

I warmly suggest to use Gradle, Maven or a similar system to deal with dependencies within your project. In this case, you can use this product by importing the following Maven dependency:

```xml
<dependency>
    <groupId>org.danilopianini</groupId>
    <artifactId>urlclassloader-util</artifactId>
    <version>VERSION_YOU_WANT_TO_USE</version>
</dependency>
```

or the following Gradle dependency:

```Gradle
compile 'org.danilopianini:urlclassloader-util:VERSION_YOU_WANT_TO_USE'
```

Alternatively, you can grab the latest jar and throw it in your classpath. In this case, be sure to include the dependencies of this project in your classpath as well.


