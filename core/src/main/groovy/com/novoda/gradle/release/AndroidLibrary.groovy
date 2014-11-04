package com.novoda.gradle.release

import org.gradle.api.artifacts.DependencySet
import org.gradle.api.artifacts.ModuleDependency
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.Usage

class AndroidLibrary implements SoftwareComponentInternal {

    final Usage runtimeUsage
    final DependencySet runtimeDependencies

    AndroidLibrary(DependencySet runtimeDependencies) {
        this.runtimeDependencies = runtimeDependencies
        runtimeUsage = new RuntimeUsage()
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
