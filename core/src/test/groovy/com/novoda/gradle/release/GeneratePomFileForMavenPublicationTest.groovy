package com.novoda.gradle.release

import com.novoda.gradle.release.test.GradleScriptTemplates
import com.novoda.gradle.release.test.TestProjectRule
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@RunWith(Parameterized.class)
class GeneratePomFileForMavenPublicationTest {

    @Parameterized.Parameters(name = "{index}: test POM descriptor for {0}")
    static Collection<Parameter> gradleVersionExpectedOutcome() {
        return [
                new Parameter(TestProjectRule.newJavaProject(templateFrom(GradleScriptTemplates.forJavaProject()))),
                new Parameter(TestProjectRule.newAndroidProject(templateFrom(GradleScriptTemplates.forAndroidProject())))
        ]
    }

    @Rule
    public TestProjectRule testProject

    GeneratePomFileForMavenPublicationTest(Parameter parameter) {
        this.testProject = parameter.rule
    }

    @Test
    void shouldContainAllNeededDependenciesInGeneratePomTask() {
        def projectDir = testProject.projectDir

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments('generatePomFileForMavenPublication', '--stacktrace')
                .forwardOutput()
                .withPluginClasspath()
                .build()

        assert result.task(':generatePomFileForMavenPublication').outcome == SUCCESS

        File pomFile = new File(projectDir, '/build/publications/maven/pom-default.xml')
        def nodes = new XmlSlurper().parse(pomFile)
        def dependencies = nodes.dependencies.dependency

        assert dependencies.find { dep -> dep.artifactId == 'hello' }.scope == 'compile'
        assert dependencies.find { dep -> dep.artifactId == 'haha' }.scope == 'compile'
        assert dependencies.find { dep -> dep.artifactId == 'world' }.scope == 'runtime'
    }

    private static final String templateFrom(String baseTemplate) {
        return """
        $baseTemplate
        
        dependencies {
            compile 'com.abc:hello:1.0.0'
            implementation 'com.xyz:world:2.0.0'
            api 'com.xxx:haha:3.0.0'
        }
        """.stripIndent()

    }

    private static class Parameter {
        final TestProjectRule rule

        Parameter(TestProjectRule rule) {
            this.rule = rule
        }

        String toString() {
            return rule.projectType
        }
    }
}
