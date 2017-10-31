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
import org.gradle.api.model.ObjectFactory

class AndroidLibrary implements SoftwareComponentInternal {

    private final UsageContext runtimeUsage

    public static AndroidLibrary newInstance(Project project) {

        ObjectFactory objectFactory = project.getObjects();
        Usage usage = objectFactory.named(Usage.class, Usage.JAVA_RUNTIME);


        def configuration = project.configurations.getByName("compile")
        return configuration ? from(configuration, usage) : empty()
    }

    static AndroidLibrary from(def configuration, Usage usage) {
        def runtimeUsage = new RuntimeUsage(configuration.dependencies, usage)
        new AndroidLibrary(runtimeUsage)
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
        private final Usage usage;

        RuntimeUsage(DomainObjectSet<Dependency> runtimeDependencies, Usage usage) {
            this.usage = usage;
            this.runtimeDependencies = runtimeDependencies
        }

        Usage getUsage() {
            return usage;
        }

        public Set<PublishArtifact> getArtifacts() {
            new LinkedHashSet<PublishArtifact>()
        }

        public Set<ModuleDependency> getDependencies() {
            runtimeDependencies.withType(ModuleDependency)
        }
    }
}
