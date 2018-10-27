package com.novoda.gradle.release

import com.novoda.gradle.test.GradleBuildResult
import com.novoda.gradle.test.GradleScriptTemplates
import com.novoda.gradle.test.TestProject
import com.novoda.gradle.truth.GradleTruth
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileTree
import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

import static com.google.common.truth.Truth.assertThat

@RunWith(Parameterized.class)
class ReleasePluginTest {

    private static final String BASE_UPLOAD_PATH = 'https://api.bintray.com/content/novoda/maven/test/1.0/com/novoda/test/1.0/test-1.0'
    private static final String SOURCES_UPLOAD_PATH = "$BASE_UPLOAD_PATH-sources.jar"
    private static final String JAVADOC_UPLOAD_PATH = "$BASE_UPLOAD_PATH-javadoc.jar"
    private static final String POM_UPLOAD_PATH = "${BASE_UPLOAD_PATH}.pom"
    private static final String JAR_UPLOAD_PATH = "${BASE_UPLOAD_PATH}.jar"
    private static final String AAR_UPLOAD_PATH = "${BASE_UPLOAD_PATH}.aar"

    private static final Map<String, GradleBuildResult> RESULTS = [:]

    @Parameterized.Parameters(name = "{0}")
    static Collection<BuildConfiguration> configurations() {
        return [
                BuildConfiguration.forAndroid('4.0', false),
                BuildConfiguration.forAndroid('4.1', true),
                BuildConfiguration.forAndroid('4.2', true),
                BuildConfiguration.forAndroid('4.3', true),
                BuildConfiguration.forAndroid('4.4', true),
                BuildConfiguration.forAndroid('4.5', false),
                BuildConfiguration.forJava('4.0', true),
                BuildConfiguration.forJava('4.1', true),
                BuildConfiguration.forJava('4.2', true),
                BuildConfiguration.forJava('4.3', true),
                BuildConfiguration.forJava('4.4', true),
                BuildConfiguration.forJava('4.5', true)
        ]
    }

    private final TestProject testProject
    private final BuildConfiguration configuration

    ReleasePluginTest(BuildConfiguration configuration) {
        this.configuration = configuration
        this.testProject = configuration.testProject
    }

    private GradleBuildResult getResult() {
        if (RESULTS[configuration.key] == null) {
            testProject.init("${this.class.canonicalName}/${configuration}/test")
            RESULTS[configuration.key] = testProject.execute('clean', 'build', 'bintrayUpload', '-PbintrayKey=key', '-PbintrayUser=user', '--stacktrace')
        }
        return RESULTS[configuration.key]
    }

    @Test
    void shouldBuildLibrary() {
        GradleTruth.assumeThat(result).isSuccess()

        GradleTruth.assertThat(result.task(':build')).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldGeneratePomFile() {
        GradleTruth.assumeThat(result).isSuccess()

        GradleTruth.assertThat(result.task(configuration.generatePomTaskName)).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldGenerateJavadocs() {
        GradleTruth.assumeThat(result).isSuccess()

        GradleTruth.assertThat(result.task(configuration.generateJavadocsTaskName)).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldPackageAllGeneratedJavadocs() {
        GradleTruth.assumeThat(result).isSuccess()

        GradleTruth.assertThat(result.task(configuration.packageJavadocsTaskName)).hasOutcome(TaskOutcome.SUCCESS)

        ConfigurableFileTree generatedFiles = testProject.fileTree('build/docs/javadoc')
        List<String> includePatterns = generatedFiles.collect { '**' + it.path - generatedFiles.dir.path }
        FileTree jarfile = testProject.zipTree('build/libs/test-javadoc.jar')
        assertThat(jarfile.matching { include includePatterns }).hasSize(includePatterns.size())
    }

    @Test
    void shouldPackageAllSources() {
        GradleTruth.assumeThat(result).isSuccess()

        GradleTruth.assertThat(result.task(configuration.packageSourcesTaskName)).hasOutcome(TaskOutcome.SUCCESS)

        ConfigurableFileTree sourceFiles = testProject.fileTree('src/main/java')
        List<String> includePatterns = sourceFiles.collect { '**' + it.path - sourceFiles.dir.path }
        FileTree jarfile = testProject.zipTree('build/libs/test-sources.jar')
        assertThat(jarfile.matching { include includePatterns }).hasSize(includePatterns.size())
    }

    @Test
    void shouldPublishToMavenLocal() {
        GradleTruth.assumeThat(result).isSuccess()

        GradleTruth.assertThat(result.task(configuration.publishToMavenLocalTaskName)).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldRunUploadTask() {
        GradleTruth.assumeThat(result).isSuccess()

        GradleTruth.assertThat(result.task(":bintrayUpload")).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldUploadSourcesJar() {
        GradleTruth.assumeThat(result).isSuccess()

        assertThat(result.output).contains(SOURCES_UPLOAD_PATH)
    }

    @Test
    void shouldUploadJavadocJar() {
        GradleTruth.assumeThat(result).isSuccess()

        assertThat(result.output).contains(JAVADOC_UPLOAD_PATH)
    }

    @Test
    void shouldUploadPomFile() {
        GradleTruth.assumeThat(result).isSuccess()

        assertThat(result.output).contains(POM_UPLOAD_PATH)
    }

    @Test
    void shouldUploadLibraryArtifact() {
        GradleTruth.assumeThat(result).isSuccess()

        assertThat(result.output).contains(configuration.libraryUploadPath)
    }

    @Test
    void shouldMatchBuildOutcome() {
        assertThat(result.success).isEqualTo(configuration.expectedBuildSuccess)
    }

    private static class BuildConfiguration {
        final String gradleVersion
        final TestProject testProject
        final boolean expectedBuildSuccess

        static BuildConfiguration forAndroid(String gradleVersion, boolean expectedBuildSuccess) {
            def additionalRunnerConfig = { GradleRunner runner -> runner.withGradleVersion(gradleVersion) }
            def buildScript = GradleScriptTemplates.forAndroidProject()
            def testProject = TestProject.newAndroidProject(buildScript, additionalRunnerConfig)
            return new BuildConfiguration(gradleVersion, testProject, expectedBuildSuccess)
        }

        static BuildConfiguration forJava(String gradleVersion, boolean expectedBuildSuccess) {
            def additionalRunnerConfig = { GradleRunner runner -> runner.withGradleVersion(gradleVersion) }
            def buildScript = GradleScriptTemplates.forJavaProject()
            def testProject = TestProject.newJavaProject(buildScript, additionalRunnerConfig)
            return new BuildConfiguration(gradleVersion, testProject, expectedBuildSuccess)
        }

        private BuildConfiguration(String gradleVersion, TestProject testProject, boolean expectedBuildSuccess) {
            this.gradleVersion = gradleVersion
            this.testProject = testProject
            this.expectedBuildSuccess = expectedBuildSuccess
        }

        @Override
        String toString() {
            return "${testProject.projectType.capitalize()} project with Gradle $gradleVersion"
        }

        String getKey() {
            return toString()
        }

        private boolean isAndroid() {
            return testProject.projectType == 'android'
        }

        private String getPublicationName() {
            return isAndroid() ? 'release' : 'maven'
        }

        String getGeneratePomTaskName() {
            return ":generatePomFileFor${publicationName.capitalize()}Publication"
        }

        String getGenerateJavadocsTaskName() {
            return isAndroid() ? ":javadoc${publicationName.capitalize()}" : ':javadoc'
        }

        String getPackageJavadocsTaskName() {
            return ":genereateJavadocsJarFor${publicationName.capitalize()}Publication"
        }

        String getPackageSourcesTaskName() {
            return ":genereateSourcesJarFor${publicationName.capitalize()}Publication"
        }

        String getPublishToMavenLocalTaskName() {
            return ":publish${publicationName.capitalize()}PublicationToMavenLocal"
        }

        String getLibraryUploadPath() {
            return isAndroid() ? AAR_UPLOAD_PATH : JAR_UPLOAD_PATH
        }
    }
}
