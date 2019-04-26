package com.novoda.gradle.test

class BuildFolder {

    private final File rootDir

    BuildFolder(String path = '') {
        this.rootDir = new File(Fixtures.BUILD_DIR, path)
    }

    File getRootDir() {
        rootDir
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
