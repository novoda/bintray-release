package com.novoda.gradle.test

final class Fixtures {

    public static final File BUILD_DIR = findBuildDir()
    public static final File ROOT_DIR = BUILD_DIR.parentFile

    private static File findBuildDir() {
        def start = new File(getResource('.').file)
        if (start.path.endsWith('build/classes/groovy/test')) {
            return start.parentFile.parentFile.parentFile
        } else if (start.path.endsWith('out/test/classes')) {
            return start.parentFile.parentFile
        } else {
            throw new UnsupportedOperationException("Unable to identify build folder from path: $start")
        }
    }

    private static URL getResource(String resourceName) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader() ?: BuildFolder.class.getClassLoader()
        URL url = loader.getResource(resourceName)
        if (url == null) {
            throw new IllegalArgumentException("resource ${resourceName} not found.")
        }
        return url
    }
}
