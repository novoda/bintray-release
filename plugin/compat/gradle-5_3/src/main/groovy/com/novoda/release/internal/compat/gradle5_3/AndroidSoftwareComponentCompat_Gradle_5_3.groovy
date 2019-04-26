package com.novoda.release.internal.compat.gradle5_3

import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.attributes.ImmutableAttributes
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.internal.java.usagecontext.LazyConfigurationUsageContext
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

/**
 * This implementation of {@code SoftwareComponentInternal} is heavily inspired by {@code JavaLibrary},
 * see: https://github.com/gradle/gradle/blob/v5.3.0/subprojects/plugins/src/main/java/org/gradle/api/internal/java/JavaLibrary.java
 */
class AndroidSoftwareComponentCompat_Gradle_5_3 implements SoftwareComponentInternal {

    private final Set<PublishArtifact> artifacts = new LinkedHashSet<PublishArtifact>()
    private final UsageContext runtimeUsage
    private final UsageContext compileUsage
    protected final ConfigurationContainer configurations
    protected final ObjectFactory objectFactory
    protected final ImmutableAttributesFactory attributesFactory

    @Inject
    AndroidSoftwareComponentCompat_Gradle_5_3(ObjectFactory objectFactory, ConfigurationContainer configurations, ImmutableAttributesFactory attributesFactory) {
        this.configurations = configurations
        this.objectFactory = objectFactory
        this.attributesFactory = attributesFactory
        this.runtimeUsage = createRuntimeUsageContext()
        this.compileUsage = createCompileUsageContext()
    }

    @Override
    String getName() {
        return 'android'
    }

    @Override
    Set<UsageContext> getUsages() {
        return ([runtimeUsage, compileUsage] as Set).asImmutable()
    }

    private UsageContext createRuntimeUsageContext() {
        ImmutableAttributes attributes = attributesFactory.of(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, Usage.JAVA_RUNTIME))
        return new LazyConfigurationUsageContext('runtime', 'implementation', artifacts, configurations, attributes)
    }

    private UsageContext createCompileUsageContext() {
        ImmutableAttributes attributes = attributesFactory.of(Usage.USAGE_ATTRIBUTE, objectFactory.named(Usage.class, Usage.JAVA_API))
        return new LazyConfigurationUsageContext('api', 'api', artifacts, configurations, attributes)
    }
}
