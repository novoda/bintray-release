package com.novoda.gradle.release

import com.novoda.gradle.test.GradleScriptTemplates
import com.novoda.gradle.test.TestProject
import com.novoda.gradle.truth.GradleTruth
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
    public final TestProject testProject

    private final String publicationName

    GeneratePomFileForMavenPublicationTest(Parameter parameter) {
        this.testProject = parameter.testProject
        this.publicationName = parameter.publicationName
    }

    @Test
    void shouldContainAllNeededDependenciesInGeneratePomTask() {
        def generatingTaskName = ":generatePomFileFor${publicationName.capitalize()}Publication"

        def result = testProject.execute(generatingTaskName, '--stacktrace')

        GradleTruth.assertThat(result.task(generatingTaskName)).hasOutcome(SUCCESS)
        assertThat(dependencyScopeFor('hello')).isEqualTo('compile')
        assertThat(dependencyScopeFor('haha')).isEqualTo('compile')
        assertThat(dependencyScopeFor('world')).isEqualTo('runtime')
    }

    private def dependencyScopeFor(String artifactId) {
        return dependenciesFromPOM().find { dep -> dep.artifactId == artifactId }.scope
    }

    private def dependenciesFromPOM() {
        File pomFile = new File(testProject.projectDir, "/build/publications/$publicationName/pom-default.xml")
        def nodes = new XmlSlurper().parse(pomFile)
        return nodes.dependencies.dependency
    }

    private static class Parameter {

        static Parameter forJavaProject() {
            return new Parameter(
                    TestProject.newJavaProject(templateFrom(GradleScriptTemplates.forJavaProject())),
                    'maven')
        }

        static Parameter forAndroidProject() {
            return new Parameter(
                    TestProject.newAndroidProject(templateFrom(GradleScriptTemplates.forAndroidProject())),
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

        final TestProject testProject
        final String publicationName

        Parameter(TestProject testProject, String publicationName) {
            this.testProject = testProject
            this.publicationName = publicationName
        }

        String toString() {
            return testProject.projectType
        }
    }
}
