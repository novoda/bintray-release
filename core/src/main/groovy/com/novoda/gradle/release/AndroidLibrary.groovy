package com.novoda.gradle.release

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

class AndroidLibrary implements SoftwareComponentInternal {

    private final Set<UsageContext> usages

    AndroidLibrary(Project project) {
        this.usages = new DefaultDomainObjectSet<UsageContext>(UsageContext)

        ObjectFactory objectFactory = project.getObjects()
        Usage api = objectFactory.named(Usage.class, Usage.JAVA_API)
        Usage runtime = objectFactory.named(Usage.class, Usage.JAVA_RUNTIME)

        addUsageContextFromConfiguration(project, "compile", api)
        addUsageContextFromConfiguration(project, "api", api)
        addUsageContextFromConfiguration(project, "implementation", runtime)
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
