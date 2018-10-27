package com.novoda.gradle.release

import com.novoda.gradle.test.GradleBuildResult
import com.novoda.gradle.test.TestProject
import com.novoda.gradle.truth.GradleTruth
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileTree
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class BintrayUploadJavaTest {

    private static final String BASE_UPLOAD_PATH = 'https://api.bintray.com/content/novoda/maven/test/1.0/com/novoda/test/1.0/test-1.0'
    private static final String SOURCES_UPLOAD_PATH = "$BASE_UPLOAD_PATH-sources.jar"
    private static final String JAVADOC_UPLOAD_PATH = "$BASE_UPLOAD_PATH-javadoc.jar"
    private static final String POM_UPLOAD_PATH = "${BASE_UPLOAD_PATH}.pom"
    private static final String JAR_UPLOAD_PATH = "${BASE_UPLOAD_PATH}.jar"

    private static TestProject testProject
    private static GradleBuildResult result

    @Before
    void setUp() {
        if (testProject == null) {
            testProject = TestProject.newJavaProject()
            testProject.init("${this.class.canonicalName}/test")
            result = testProject.execute('clean', 'build', ":bintrayUpload", '-PbintrayUser=U', '-PbintrayKey=K', '-PdryRun=true', '--stacktrace')
        }
    }

    @Test
    void shouldBuildLibrary() {
        GradleTruth.assertThat(result.task(':build')).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldGeneratePomFile() {
        GradleTruth.assertThat(result.task(':generatePomFileForMavenPublication')).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldGenerateJavadocs() {
        GradleTruth.assertThat(result.task(':javadoc')).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldPackageAllGeneratedJavadocs() {
        GradleTruth.assertThat(result.task(':mavenJavadocJar')).hasOutcome(TaskOutcome.SUCCESS)

        ConfigurableFileTree generatedFiles = testProject.fileTree('build/docs/javadoc')
        List<String> includePatterns = generatedFiles.collect { '**' + it.path - generatedFiles.dir.path }
        FileTree jarfile = testProject.zipTree('build/libs/test-javadoc.jar')
        assertThat(jarfile.matching { include includePatterns }).hasSize(includePatterns.size())
    }

    @Test
    void shouldPackageAllSources() {
        GradleTruth.assertThat(result.task(':mavenSourcesJar')).hasOutcome(TaskOutcome.SUCCESS)

        ConfigurableFileTree sourceFiles = testProject.fileTree('src/main/java')
        List<String> includePatterns = sourceFiles.collect { '**' + it.path - sourceFiles.dir.path }
        FileTree jarfile = testProject.zipTree('build/libs/test-sources.jar')
        assertThat(jarfile.matching { include includePatterns }).hasSize(includePatterns.size())
    }

    @Test
    void shouldPublishToMavenLocal() {
        GradleTruth.assertThat(result.task(':publishMavenPublicationToMavenLocal')).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldRunUploadTask() {
        GradleTruth.assertThat(result.task(":bintrayUpload")).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldUploadSourcesJar() {
        assertThat(result.output).contains(SOURCES_UPLOAD_PATH)
    }

    @Test
    void shouldUploadJavadocJar() {
        assertThat(result.output).contains(JAVADOC_UPLOAD_PATH)
    }

    @Test
    void shouldUploadPomFile() {
        assertThat(result.output).contains(POM_UPLOAD_PATH)
    }

    @Test
    void shouldUploadLibraryJar() {
        assertThat(result.output).contains(JAR_UPLOAD_PATH)
    }

    @Test
    void shouldBuildSuccessfully() {
        GradleTruth.assertThat(result).isSuccess()
    }
}
