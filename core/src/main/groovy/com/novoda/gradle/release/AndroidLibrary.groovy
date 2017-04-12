package com.novoda.gradle.release

import org.gradle.api.DomainObjectSet
import org.gradle.api.Project
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.DefaultDomainObjectSet
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext

class AndroidLibrary implements SoftwareComponentInternal {

    private final UsageContext runtimeUsage

    public static AndroidLibrary newInstance(Project project) {
        def configuration = project.configurations.getByName("compile")
        return configuration ? from(configuration) : empty()
    }

    static AndroidLibrary from(def configuration) {
        def usage = new RuntimeUsage(configuration.dependencies)
        new AndroidLibrary(usage)
    }

    static AndroidLibrary empty() {
        def usage = new RuntimeUsage(new DefaultDomainObjectSet<Dependency>(Dependency))
        new AndroidLibrary(usage)
    }

    AndroidLibrary(UsageContext runtimeUsage) {
        this.runtimeUsage = runtimeUsage
    }

    public String getName() {
        return "android"
    }

    public Set<UsageContext> getUsages() {
        return Collections.singleton(runtimeUsage);
    }

    private static class RuntimeUsage implements UsageContext {

        private final DomainObjectSet<Dependency> runtimeDependencies

        RuntimeUsage(DomainObjectSet<Dependency> runtimeDependencies) {
            this.runtimeDependencies = runtimeDependencies
        }

        Usage getUsage() {
            return Usage.FOR_RUNTIME
        }

        public Set<PublishArtifact> getArtifacts() {
            new LinkedHashSet<PublishArtifact>()
        }

        public Set<ModuleDependency> getDependencies() {
            runtimeDependencies.withType(ModuleDependency)
        }
    }
}
