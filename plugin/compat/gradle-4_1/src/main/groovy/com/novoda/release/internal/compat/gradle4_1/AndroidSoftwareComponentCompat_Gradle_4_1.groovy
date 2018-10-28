package com.novoda.release.internal.compat.gradle4_1

import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.model.ObjectFactory
import org.gradle.util.GradleVersion

class AndroidSoftwareComponentCompat_Gradle_4_1 implements SoftwareComponentInternal {

    private final Set<? extends UsageContext> usageContexts
    private final ConfigurationContainer configurations

    AndroidSoftwareComponentCompat_Gradle_4_1(ObjectFactory objectFactory, ConfigurationContainer configurations) {
        this.configurations = configurations
        this.usageContexts = generateUsageContexts(objectFactory).asImmutable()
    }

    // Using the new Usage in 4.1 will make the plugin crash
    // as comparing logic is still using the old Usage.
    // For more details: https://github.com/novoda/bintray-release/pull/147
    private Set<? extends UsageContext> generateUsageContexts(ObjectFactory objectFactory) {
        def isNewerThan4_1 = GradleVersion.current() > GradleVersion.version("4.1")
        Usage runtime = objectFactory.named(Usage.class, isNewerThan4_1 ? Usage.JAVA_RUNTIME : "for runtime")
        Usage compile = objectFactory.named(Usage.class, isNewerThan4_1 ? Usage.JAVA_API : "for compile")
        RuntimeUsageContext runtimeUsage = new RuntimeUsageContext(runtime)
        CompileUsageContext compileUsage = new CompileUsageContext(compile)
        return [runtimeUsage, compileUsage] as Set<? extends UsageContext>
    }

    @Override
    Set<? extends UsageContext> getUsages() {
        return usageContexts
    }

    @Override
    String getName() {
        return 'android'
    }

    private class RuntimeUsageContext implements UsageContext {

        private final Usage usage
        private DependencySet dependencies

        RuntimeUsageContext(Usage usage) {
            this.usage = usage
        }

        @Override
        Usage getUsage() {
            return usage
        }

        @Override
        Set<PublishArtifact> getArtifacts() {
            return Collections.emptySet()
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            if (dependencies == null) {
                dependencies = configurations.getByName('implementation').getAllDependencies()
            }
            return dependencies.withType(ModuleDependency.class)
        }
    }

    private class CompileUsageContext implements UsageContext {

        private final Usage usage
        private DependencySet dependencies

        CompileUsageContext(Usage usage) {
            this.usage = usage
        }

        @Override
        Usage getUsage() {
            return usage
        }

        @Override
        Set<PublishArtifact> getArtifacts() {
            return Collections.emptySet()
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            if (dependencies == null) {
                dependencies = (configurations.findByName('api') ?: configurations.getByName('compile')).getAllDependencies()
            }
            return dependencies.withType(ModuleDependency.class)
        }
    }
}
