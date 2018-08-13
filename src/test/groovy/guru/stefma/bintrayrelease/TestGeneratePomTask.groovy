package guru.stefma.bintrayrelease

import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class TestGeneratePomTask {

    @Rule
    public TemporaryFolder testProjectDir = new TemporaryFolder()

    private File buildFile

    @Before
    void setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    @Test
    void testGeneratePomTaskForJavaLib() {
        buildFile << """            
            plugins {
                id 'java-library'
                id 'guru.stefma.bintrayrelease'
            }
            
            group = "guru.stefma"
            version = "1.0"
            publish {
                userOrg = 'stefma'
                artifactId = 'test'
                desc = 'description'
            }
        
            dependencies {
                compile 'com.abc:hello:1.0.0'
                implementation 'com.xyz:world:2.0.0'
                api 'com.xxx:haha:3.0.0'
            }
        """

        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .withArguments("generatePomFileForMavenPublication")
                .withPluginClasspath()
                .build()

        assert result.task(":generatePomFileForMavenPublication").outcome == SUCCESS

        File pomFile = new File(testProjectDir.root, '/build/publications/maven/pom-default.xml')
        def nodes = new XmlSlurper().parse(pomFile)
        def dependencies = nodes.dependencies.dependency

        assert dependencies.size() == 3
        assert dependencies.find { dep -> dep.artifactId == "hello" && dep.scope == "compile" } != null
        assert dependencies.find { dep -> dep.artifactId == "haha" && dep.scope == "compile" } != null
        assert dependencies.find { dep -> dep.artifactId == "world" && dep.scope == "runtime" } != null
    }
}