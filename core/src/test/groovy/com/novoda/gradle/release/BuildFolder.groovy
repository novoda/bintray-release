package com.novoda.gradle.release


import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class BuildFolder implements TestRule {

    private File rootDir

    BuildFolder(String path = '') {
        File buildDir = new File(getResource('.').file).parentFile.parentFile.parentFile
        assert buildDir.path.endsWith('core/build')
        rootDir = new File(buildDir, path)
    }

    private static URL getResource(String resourceName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader() ?: BuildFolder.class.getClassLoader()
        URL url = loader.getResource(resourceName)
        if (url == null) {
            throw new IllegalArgumentException("resource ${resourceName} not found.")
        }
        return url
    }

    File newFolder(String path) {
        File folder = new File(rootDir, path)
        folder.mkdirs()
        return folder
    }

    File newFile(File parent, String path) {
        File file = new File(parent, path)
        file.parentFile.mkdirs()
        file.createNewFile()
        return file
    }

    File newFile(String path) {
        return newFile(rootDir, path)
    }

    File getRoot() {
        return rootDir
    }

    @Override
    Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override
            void evaluate() throws Throwable {
                rootDir.mkdirs()
                base.evaluate()
                rootDir.delete()
            }
        }
    }
}
