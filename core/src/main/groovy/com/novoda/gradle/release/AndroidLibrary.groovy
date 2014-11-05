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
    private final DomainObjectSet<Dependency> runtimeDependencies

    public static AndroidLibrary newInstance(Project project) {
        def configuration = project.configurations.getAll().find { it.dependencies }
        return configuration ? from(configuration) : empty()
    }

    static AndroidLibrary from(def configuration) {
        new AndroidLibrary(configuration.dependencies)
    }

    static AndroidLibrary empty() {
        new AndroidLibrary(new DefaultDomainObjectSet(Dependency))
    }

    AndroidLibrary(DomainObjectSet<Dependency> runtimeDependencies) {
        this(runtimeDependencies, new RuntimeUsage())
    }

    AndroidLibrary(DomainObjectSet<Dependency> runtimeDependencies, Usage runtimeUsage) {
        this.runtimeDependencies = runtimeDependencies
        this.runtimeUsage = runtimeUsage
    }

    public String getName() {
        return "android"
    }

    public Set<Usage> getUsages() {
        return Collections.singleton(runtimeUsage);
    }

    private class RuntimeUsage implements Usage {
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
