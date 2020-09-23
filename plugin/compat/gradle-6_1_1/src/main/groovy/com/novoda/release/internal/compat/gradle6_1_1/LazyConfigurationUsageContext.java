package com.novoda.release.internal.compat.gradle6_1_1;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.internal.attributes.ImmutableAttributes;
import org.gradle.api.plugins.internal.AbstractConfigurationUsageContext;

import java.util.Set;

public class LazyConfigurationUsageContext extends AbstractConfigurationUsageContext {
    private final String configurationName;
    private final ConfigurationContainer configurations;

    public LazyConfigurationUsageContext(String name, String configurationName, Set<PublishArtifact> artifacts, ConfigurationContainer configurations, ImmutableAttributes attributes) {
        super(name, attributes, artifacts);
        this.configurationName = configurationName;
        this.configurations = configurations;
    }

    protected Configuration getConfiguration() {
        return this.configurations.getByName(this.configurationName);
    }
}