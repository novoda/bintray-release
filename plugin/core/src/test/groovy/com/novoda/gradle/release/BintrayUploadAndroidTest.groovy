package com.novoda.gradle.release


import com.novoda.gradle.test.GradleBuildResult
import com.novoda.gradle.test.TestProjectRule
import com.novoda.gradle.truth.GradleTruth
import org.gradle.api.file.ConfigurableFileTree
import org.gradle.api.file.FileTree
import org.gradle.testkit.runner.TaskOutcome
import org.junit.Before
import org.junit.ClassRule
import org.junit.Test

import static com.google.common.truth.Truth.assertThat

class BintrayUploadAndroidTest {

    private static final String BASE_UPLOAD_PATH = 'https://api.bintray.com/content/novoda/maven/test/1.0/com/novoda/test/1.0/test-1.0'
    private static final String SOURCES_UPLOAD_PATH = "$BASE_UPLOAD_PATH-sources.jar"
    private static final String JAVADOC_UPLOAD_PATH = "$BASE_UPLOAD_PATH-javadoc.jar"
    private static final String POM_UPLOAD_PATH = "${BASE_UPLOAD_PATH}.pom"
    private static final String AAR_UPLOAD_PATH = "${BASE_UPLOAD_PATH}.aar"

    @ClassRule
    public static TestProjectRule testProject = TestProjectRule.newAndroidProject()
    private static GradleBuildResult result

    @Before
    void setUp() {
        if (result == null) {
            result = testProject.execute('clean', 'build', ":bintrayUpload", '-PbintrayUser=U', '-PbintrayKey=K', '-PdryRun=true', '--stacktrace')
        }
    }

    @Test
    void shouldBuildLibrary() {
        GradleTruth.assertThat(result.task(':build')).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldGeneratePomFile() {
        GradleTruth.assertThat(result.task(':generatePomFileForReleasePublication')).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldGenerateJavadocs() {
        GradleTruth.assertThat(result.task(':releaseAndroidJavadocs')).hasOutcome(TaskOutcome.SUCCESS)
    }

    @Test
    void shouldPackageAllGeneratedJavadocs() {
        GradleTruth.assertThat(result.task(':releaseAndroidJavadocsJar')).hasOutcome(TaskOutcome.SUCCESS)

        ConfigurableFileTree generatedFiles = testProject.fileTree('build/docs/javadoc')
        List<String> includePatterns = generatedFiles.collect { '**' + it.path - generatedFiles.dir.path }
        FileTree jarfile = testProject.zipTree('build/libs/test-javadoc.jar')
        assertThat(jarfile.matching { include includePatterns }).hasSize(includePatterns.size())
    }

    @Test
    void shouldPackageAllSources() {
        GradleTruth.assertThat(result.task(':releaseAndroidSourcesJar')).hasOutcome(TaskOutcome.SUCCESS)

        ConfigurableFileTree sourceFiles = testProject.fileTree('src/main/java')
        List<String> includePatterns = sourceFiles.collect { '**' + it.path - sourceFiles.dir.path }
        FileTree jarfile = testProject.zipTree('build/libs/test-sources.jar')
        assertThat(jarfile.matching { include includePatterns }).hasSize(includePatterns.size())
    }

    @Test
    void shouldPublishToMavenLocal() {
        GradleTruth.assertThat(result.task(':publishReleasePublicationToMavenLocal')).hasOutcome(TaskOutcome.SUCCESS)
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
    void shouldUploadLibraryAar() {
        assertThat(result.output).contains(AAR_UPLOAD_PATH)
    }

    @Test
    void shouldBuildSuccessfully() {
        GradleTruth.assertThat(result).isSuccess()
    }
}