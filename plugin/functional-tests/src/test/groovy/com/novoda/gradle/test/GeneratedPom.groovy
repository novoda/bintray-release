package com.novoda.gradle.test

class GeneratedPom {
    private final def nodes

    static GeneratedPom from(File file) {
        def nodes = new XmlSlurper().parse(file)
        return new GeneratedPom(nodes)
    }

    private GeneratedPom(nodes) {
        this.nodes = nodes
    }

    def getDependencies() {
        return nodes.dependencies.dependency
    }

    def dependency(String artifactId) {
        return dependencies.find { it.artifactId == artifactId }
    }
}
