package com.novoda.gradle.test


import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class BuildFolderRule implements TestRule {

    private File rootDir

    BuildFolderRule(String path = '') {
        def start = new File(getResource('.').file)
        if (start.path.endsWith('build/classes/groovy/test')) {
            rootDir = new File(start.parentFile.parentFile.parentFile, path)
        } else if (start.path.endsWith('out/test/classes')) {
            rootDir = new File(start.parentFile.parentFile.parentFile, "build/$path")
        } else {
            throw new UnsupportedOperationException("Unable to identify build folder from path: $start")
        }
    }

    private static URL getResource(String resourceName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader() ?: BuildFolderRule.class.getClassLoader()
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
            }
        }
    }
}
