package com.novoda.gradle.release

import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.internal.DefaultDomainObjectSet
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.Usage

class AndroidLibrary implements SoftwareComponentInternal {

    private final Usage runtimeUsage

    public static AndroidLibrary newInstance(Project project, variant) {
        DefaultDomainObjectSet<Dependency> dependencies = new DefaultDomainObjectSet<Dependency>(Dependency)
        def configuration = project.configurations.getByName("compile")
        if (configuration != null) dependencies.addAll(configuration.dependencies);
        variant.productFlavors.each { flavor ->
            configuration = project.configurations.getByName(flavor.name+"Compile")
            if (configuration != null)
                dependencies.addAll(configuration.dependencies)
        }
        configuration = project.configurations.getByName(variant.buildType.name+"Compile")
        if (configuration != null)
            dependencies.addAll(configuration.dependencies)
        return configuration ? from(dependencies) : empty()
    }

    static AndroidLibrary from(dependencies) {
        def usage = new RuntimeUsage(dependencies)
        new AndroidLibrary(usage)
    }

    static AndroidLibrary empty() {
        def usage = new RuntimeUsage(new DefaultDomainObjectSet<Dependency>(Dependency))
        new AndroidLibrary(usage)
    }

    AndroidLibrary(Usage runtimeUsage) {
        this.runtimeUsage = runtimeUsage
    }

    public String getName() {
        return "android"
    }

    public Set<Usage> getUsages() {
        return Collections.singleton(runtimeUsage);
    }

    private static class RuntimeUsage implements Usage {

        final DomainObjectSet<Dependency> runtimeDependencies

        RuntimeUsage(DomainObjectSet<Dependency> runtimeDependencies) {
            this.runtimeDependencies = runtimeDependencies
        }

        public String getName() {
            "runtime"
        }

        public Set<PublishArtifact> getArtifacts() {
            new LinkedHashSet<PublishArtifact>()
        }

        public Set<ModuleDependency> getDependencies() {
            runtimeDependencies.withType(ModuleDependency)
        }
    }
}
