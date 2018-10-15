package com.novoda.gradle.release;

import org.gradle.api.DomainObjectSet;
import org.gradle.api.artifacts.*;
import org.gradle.api.attributes.AttributeContainer;
import org.gradle.api.attributes.Usage;
import org.gradle.api.capabilities.Capability;
import org.gradle.api.internal.artifacts.configurations.Configurations;
import org.gradle.api.internal.attributes.ImmutableAttributes;
import org.gradle.api.internal.attributes.ImmutableAttributesFactory;
import org.gradle.api.internal.component.SoftwareComponentInternal;
import org.gradle.api.internal.component.UsageContext;
import org.gradle.api.model.ObjectFactory;

import javax.inject.Inject;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class AndroidLibrary implements SoftwareComponentInternal {

    private final Set<PublishArtifact> artifacts = new LinkedHashSet<PublishArtifact>();
    private final UsageContext runtimeUsage;
    private final UsageContext compileUsage;
    private final ConfigurationContainer configurations;
    private final ObjectFactory objectFactory;
    private final ImmutableAttributesFactory attributesFactory;

    @Inject
    public AndroidLibrary(ObjectFactory objectFactory, ConfigurationContainer configurations, ImmutableAttributesFactory attributesFactory) {
        this.configurations = configurations;
        this.objectFactory = objectFactory;
        this.attributesFactory = attributesFactory;
        this.runtimeUsage = createRuntimeUsageContext();
        this.compileUsage = createCompileUsageContext();
    }

    public String getName() {
        return "android";
    }

    public Set<UsageContext> getUsages() {
        HashSet<UsageContext> usages = new HashSet<UsageContext>(2);
        usages.add(runtimeUsage);
        usages.add(compileUsage);
        return usages;
    }

    private abstract class AbstractUsageContext implements UsageContext {
        private final Usage usage;
        private final ImmutableAttributes attributes;

        AbstractUsageContext(String usageName) {
            this.usage = objectFactory.named(Usage.class, usageName);
            this.attributes = attributesFactory.of(Usage.USAGE_ATTRIBUTE, usage);
        }

        @Override
        public AttributeContainer getAttributes() {
            return attributes;
        }

        @Override
        public Usage getUsage() {
            return usage;
        }

        public Set<PublishArtifact> getArtifacts() {
            return artifacts;
        }
    }

    private UsageContext createRuntimeUsageContext() {
        return new ConfigurationUsageContext(Usage.JAVA_RUNTIME, "runtime", "implementation");
    }

    private UsageContext createCompileUsageContext() {
        return new ConfigurationUsageContext(Usage.JAVA_API, "api", "api");
    }

    private class ConfigurationUsageContext extends AbstractUsageContext {
        private final String name;
        private final String configurationName;
        private DomainObjectSet<ModuleDependency> dependencies;
        private DomainObjectSet<DependencyConstraint> dependencyConstraints;
        private Set<? extends Capability> capabilities;
        private Set<ExcludeRule> excludeRules;


        ConfigurationUsageContext(String usageName, String name, String configurationName) {
            super(usageName);
            this.name = name;
            this.configurationName = configurationName;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Set<ModuleDependency> getDependencies() {
            if (dependencies == null) {
                dependencies = getConfiguration().getIncoming().getDependencies().withType(ModuleDependency.class);
            }
            return dependencies;
        }

        @Override
        public Set<? extends DependencyConstraint> getDependencyConstraints() {
            if (dependencyConstraints == null) {
                dependencyConstraints = getConfiguration().getIncoming().getDependencyConstraints();
            }
            return dependencyConstraints;
        }

        @Override
        public Set<? extends Capability> getCapabilities() {
            if (capabilities == null) {
                this.capabilities = new HashSet<Capability>(Configurations.collectCapabilities(getConfiguration(),
                        new HashSet<Capability>(),
                        new HashSet<Configuration>()));
            }
            return capabilities;
        }

        @Override
        public Set<ExcludeRule> getGlobalExcludes() {
            if (excludeRules == null) {
                this.excludeRules = new HashSet<ExcludeRule>(getConfiguration().getExcludeRules());
            }
            return excludeRules;
        }

        private Configuration getConfiguration() {
            return configurations.getByName(configurationName);
        }
    }

}
