package com.novoda.gradle.release.test


import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestProjectRule implements TestRule {

    private enum ProjectType {
        JAVA, ANDROID
    }

    private final ProjectType project
    private BuildFolderRule tempFolder
    private String buildScript

    static TestProjectRule newJavaProject(String buildScript = null) {
        return new TestProjectRule(ProjectType.JAVA, buildScript)
    }

    static TestProjectRule newAndroidProject(String buildScript = null) {
        return new TestProjectRule(ProjectType.ANDROID, buildScript)
    }

    private TestProjectRule(ProjectType project, String buildScript) {
        this.project = project
        this.buildScript = buildScript
    }

    @Override
    Statement apply(Statement base, Description description) {
        tempFolder = new BuildFolderRule("test-projects/${description.testClass.canonicalName}/${description.methodName}")
        def statement = new Statement() {
            @Override
            void evaluate() throws Throwable {
                createSourceCode()
                createAndroidManifest()
                createBuildScript()
                base.evaluate()
            }
        }
        return tempFolder.apply(statement, description)
    }

    File getProjectDir() {
        tempFolder.root
    }

    private String createSourceCode() {
        new File(tempFolder.root, "src/main/java/HelloWorld.java").with {
            getParentFile().mkdirs()
            text = "public class HelloWorld {}"
        }
    }

    private void createAndroidManifest() {
        if (project == ProjectType.ANDROID) {
            new File(tempFolder.root, "/src/main/AndroidManifest.xml").with {
                getParentFile().mkdirs()
                text = "<manifest package=\"com.novoda.test\"/>"
            }
        }
    }

    private void createBuildScript() {
        File gradleScript = new File(tempFolder.root, "build.gradle")

        // If custom buildScript provided. Use it
        if (buildScript != null) {
            gradleScript.text = buildScript
            return
        }

        // ... otherwise use the Templates
        switch (project) {
            case ProjectType.JAVA:
                gradleScript.text = GradleScriptTemplates.java()
                break
            case ProjectType.ANDROID:
                gradleScript.text = GradleScriptTemplates.android()
                break
            default:
                throw new IllegalArgumentException("$project should be a valid value!")
        }
    }

    String getProjectType() {
        return project.name().toLowerCase()
    }
}
