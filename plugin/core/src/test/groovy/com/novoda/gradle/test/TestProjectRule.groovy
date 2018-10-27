package com.novoda.gradle.test


import org.gradle.api.Action
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestProjectRule implements TestRule {

    private static final Action<GradleRunner> NONE = {}

    private enum ProjectType {
        JAVA, ANDROID
    }

    private final ProjectType project
    private BuildFolderRule tempFolder
    private String buildScript
    private Action<GradleRunner> additionalRunnerConfig

    static TestProjectRule newJavaProject(String buildScript = GradleScriptTemplates.forJavaProject(), Action<GradleRunner> additionalRunnerConfig = NONE) {
        return new TestProjectRule(ProjectType.JAVA, buildScript, additionalRunnerConfig)
    }

    static TestProjectRule newAndroidProject(String buildScript = GradleScriptTemplates.forAndroidProject(), Action<GradleRunner> additionalConfig = NONE) {
        return new TestProjectRule(ProjectType.ANDROID, buildScript, additionalConfig)
    }

    private TestProjectRule(ProjectType project, String buildScript, Action<GradleRunner> additionalRunnerConfig) {
        this.project = project
        this.buildScript = buildScript
        this.additionalRunnerConfig = additionalRunnerConfig
    }

    @Override
    Statement apply(Statement base, Description description) {
        tempFolder = new BuildFolderRule("test-projects/${description.testClass.canonicalName}/${description.methodName ?: ''}")
        def statement = new Statement() {
            @Override
            void evaluate() throws Throwable {
                createSourceCode()
                createAndroidManifest()
                createBuildScript()
                createSettingsScript()
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
        new File(tempFolder.root, "build.gradle").with {
            text = buildScript
        }
    }

    private void createSettingsScript() {
        new File(tempFolder.root, 'settings.gradle').with {
            text = "rootProject.name = 'test'"
        }
    }

    String getProjectType() {
        return project.name().toLowerCase()
    }

    GradleBuildResult execute(String... arguments) {
        def runner = GradleRunner.create()
                .forwardOutput()
                .withPluginClasspath()
                .withProjectDir(projectDir)
        additionalRunnerConfig.execute(runner)
        runner.withArguments(arguments)

        try {
            return new GradleBuildResult(runner.build(), true)
        } catch (UnexpectedBuildFailure e) {
            return new GradleBuildResult(e.buildResult, false)
        }
    }

    File buildDir() {
        return new File(projectDir, 'build')
    }
}
