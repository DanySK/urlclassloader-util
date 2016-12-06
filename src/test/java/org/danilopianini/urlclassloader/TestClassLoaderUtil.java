package org.danilopianini.urlclassloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 */
public class TestClassLoaderUtil {
    private static final String WORKDIR = System.getProperty("user.home") + "/testURLClassLoader";
    private static final String FILENAME = "/myTestFile.md";

    /**
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws Exception
     */
    @Test
    public void test() throws FileNotFoundException, IOException {
        final File theFolder = new File(WORKDIR);
        if (!theFolder.mkdirs()) {
            Assert.fail("Could not create the directory structure required for testing.");
        }
        IOUtils.copy(TestClassLoaderUtil.class.getResourceAsStream("/test.md"), new FileOutputStream(WORKDIR + FILENAME));
        Assert.assertNull(res());
        URLClassLoaderUtil.addFirst(WORKDIR, cl());
        Assert.assertNotNull(res());
        URLClassLoaderUtil.remove(WORKDIR, cl());
        Assert.assertNull(res());
        URLClassLoaderUtil.addLast(WORKDIR, cl());
        Assert.assertNotNull(res());
    }

    private static ClassLoader cl() {
        return TestClassLoaderUtil.class.getClassLoader();
    }
    private static URL res() {
        return TestClassLoaderUtil.class.getResource(FILENAME);
    }

}
