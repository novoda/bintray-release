/*
 * Copyright 2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.novoda.release.internal.compat.gradle6_0;

import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.artifacts.PublishArtifact;
import org.gradle.api.internal.attributes.ImmutableAttributes;
import org.gradle.api.plugins.internal.AbstractConfigurationUsageContext;

import java.util.Set;

/**
 * LazyConfigurationUsageContext has been deprecated on Gradle 6.x.
 * I've copied this class from the Gradle repo v5.6.4 to make it work without more changes.
 */
public class LazyConfigurationUsageContext extends AbstractConfigurationUsageContext {
    private final String configurationName;
    private final ConfigurationContainer configurations;

    public LazyConfigurationUsageContext(String name,
                                         String configurationName,
                                         Set<PublishArtifact> artifacts,
                                         ConfigurationContainer configurations,
                                         ImmutableAttributes attributes) {
        super(name, attributes, artifacts);
        this.configurationName = configurationName;
        this.configurations = configurations;
    }

    @Override
    protected Configuration getConfiguration() {
        return configurations.getByName(configurationName);
    }
}
