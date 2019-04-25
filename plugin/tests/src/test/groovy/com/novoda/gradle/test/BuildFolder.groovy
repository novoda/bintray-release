package com.novoda.gradle.test

class BuildFolder {

    private Closure<File> rootDirProvider

    BuildFolder(String path = '') {
        def start = new File(getResource('.').file)
        if (start.path.endsWith('build/classes/groovy/test')) {
            rootDir(new File(start.parentFile.parentFile.parentFile, path))
        } else if (start.path.endsWith('out/test/classes')) {
            rootDir(new File(start.parentFile.parentFile.parentFile, "build/$path"))
        } else {
            throw new UnsupportedOperationException("Unable to identify build folder from path: $start")
        }
    }

    private void rootDir(File file) {
        rootDirProvider = {
            file.mkdirs()
            return file
        }.memoize()
    }

    private static URL getResource(String resourceName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader() ?: BuildFolder.class.getClassLoader()
        URL url = loader.getResource(resourceName)
        if (url == null) {
            throw new IllegalArgumentException("resource ${resourceName} not found.")
        }
        return url
    }

    File getRootDir() {
        rootDirProvider.call()
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
}
