package com.novoda.gradle.release

import com.novoda.gradle.release.test.GradleScriptTemplates
import com.novoda.gradle.release.test.TestProjectRule
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static com.google.common.truth.Truth.assertThat
import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@RunWith(Parameterized.class)
class GeneratePomFileForMavenPublicationTest {

    @Parameterized.Parameters(name = "{index}: test POM descriptor for {0}")
    static Collection<Parameter> gradleVersionExpectedOutcome() {
        return [Parameter.forJavaProject(), Parameter.forAndroidProject()]
    }

    @Rule
    public final TestProjectRule testProject

    private final String publicationName

    GeneratePomFileForMavenPublicationTest(Parameter parameter) {
        this.testProject = parameter.rule
        this.publicationName = parameter.publicationName
    }

    @Test
    void shouldContainAllNeededDependenciesInGeneratePomTask() {
        def projectDir = testProject.projectDir
        def generatingTaskName = ":generatePomFileFor${publicationName.capitalize()}Publication"

        def result = GradleRunner.create()
                .withProjectDir(projectDir)
                .withArguments(generatingTaskName, '--stacktrace')
                .forwardOutput()
                .withPluginClasspath()
                .build()

        assertThat(result.task(generatingTaskName).outcome).isEqualTo(SUCCESS)

        File pomFile = new File(projectDir, "/build/publications/$publicationName/pom-default.xml")
        def nodes = new XmlSlurper().parse(pomFile)
        def dependencies = nodes.dependencies.dependency

        assertThat(dependencies.find { dep -> dep.artifactId == 'hello' }.scope).isEqualTo('compile')
        assertThat(dependencies.find { dep -> dep.artifactId == 'haha' }.scope).isEqualTo('compile')
        assertThat(dependencies.find { dep -> dep.artifactId == 'world' }.scope).isEqualTo('runtime')
    }

    private static class Parameter {

        static Parameter forJavaProject() {
            return new Parameter(
                    TestProjectRule.newJavaProject(templateFrom(GradleScriptTemplates.forJavaProject())),
                    'maven')
        }

        static Parameter forAndroidProject() {
            return new Parameter(
                    TestProjectRule.newAndroidProject(templateFrom(GradleScriptTemplates.forAndroidProject())),
                    'release')
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

        final TestProjectRule rule
        final String publicationName

        Parameter(TestProjectRule rule, String publicationName) {
            this.rule = rule
            this.publicationName = publicationName
        }

        String toString() {
            return rule.projectType
        }
    }
}
