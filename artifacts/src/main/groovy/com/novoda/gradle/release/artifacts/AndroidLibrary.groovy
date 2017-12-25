package com.novoda.gradle.release.artifacts

import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.UnknownDomainObjectException
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.DefaultDomainObjectSet
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.model.ObjectFactory
import org.gradle.util.GradleVersion

class AndroidLibrary implements SoftwareComponentInternal {

    private final String CONF_COMPILE = "compile"
    private final String CONF_API = "api"
    private final String CONF_IMPLEMENTATION = "implementation"

    private final Set<UsageContext> usages = new DefaultDomainObjectSet<UsageContext>(UsageContext)

    AndroidLibrary(Project project) {
        ObjectFactory objectFactory = project.getObjects()

        // Using the new Usage in 4.1 will make the plugin crash
        // as comparing logic is still using the old Usage.
        // For more details: https://github.com/novoda/bintray-release/pull/147
        def isNewerThan4_1 = GradleVersion.current() > GradleVersion.version("4.1")
        Usage api = objectFactory.named(Usage.class, isNewerThan4_1 ? Usage.JAVA_API : "for compile")
        Usage runtime = objectFactory.named(Usage.class, isNewerThan4_1 ? Usage.JAVA_RUNTIME : "for runtime")

        addUsageContextFromConfiguration(project, CONF_COMPILE, api)
        addUsageContextFromConfiguration(project, CONF_API, api)
        addUsageContextFromConfiguration(project, CONF_IMPLEMENTATION, runtime)
    }

    String getName() {
        return "android"
    }

    Set<UsageContext> getUsages() {
        return usages
    }

    private addUsageContextFromConfiguration(Project project, String configuration, Usage usage) {
        try {
            def configurationObj = project.configurations.getByName(configuration)
            def dependency = configurationObj.dependencies
            if (!dependency.isEmpty()) {
                def libraryUsage = new LibraryUsage(dependency, usage)
                usages.add(libraryUsage)
            }
        } catch (UnknownDomainObjectException ignore) {
            // cannot find configuration
        }
    }

    private static class LibraryUsage implements UsageContext {

        private final DomainObjectSet<Dependency> dependencies
        private final Usage usage

        LibraryUsage(DomainObjectSet<Dependency> dependencies, Usage usage) {
            this.usage = usage
            this.dependencies = dependencies
        }

        Usage getUsage() {
            return usage
        }

        Set<PublishArtifact> getArtifacts() {
            new LinkedHashSet<PublishArtifact>()
        }

        Set<ModuleDependency> getDependencies() {
            dependencies.withType(ModuleDependency)
        }
    }
}
