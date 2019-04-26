package com.novoda.release.internal.compat.gradle5_2

import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.artifacts.PublishArtifact
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.internal.java.usagecontext.ConfigurationUsageContext
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

/**
 * This implementation of {@code SoftwareComponentInternal} is heavily inspired by {@code JavaLibrary},
 * see: https://github.com/gradle/gradle/blob/v5.2.0/subprojects/plugins/src/main/java/org/gradle/api/internal/java/JavaLibrary.java
 */
class AndroidSoftwareComponentCompat_Gradle_5_2 implements SoftwareComponentInternal {

    private final Set<PublishArtifact> artifacts = new LinkedHashSet<PublishArtifact>()
    private final UsageContext runtimeUsage
    private final UsageContext compileUsage
    protected final ConfigurationContainer configurations
    protected final ObjectFactory objectFactory
    protected final ImmutableAttributesFactory attributesFactory

    @Inject
    AndroidSoftwareComponentCompat_Gradle_5_2(ObjectFactory objectFactory, ConfigurationContainer configurations, ImmutableAttributesFactory attributesFactory) {
        this.configurations = configurations
        this.objectFactory = objectFactory
        this.attributesFactory = attributesFactory
        this.runtimeUsage = createRuntimeUsageContext()
        this.compileUsage = createCompileUsageContext()
    }

    @Override
    Set<? extends UsageContext> getUsages() {
        return ([runtimeUsage, compileUsage] as Set).asImmutable()
    }

    @Override
    String getName() {
        return 'android'
    }

    private UsageContext createRuntimeUsageContext() {
        return new ConfigurationUsageContext(Usage.JAVA_RUNTIME, 'runtime', 'implementation', artifacts, configurations, objectFactory, attributesFactory);
    }

    private UsageContext createCompileUsageContext() {
        return new ConfigurationUsageContext(Usage.JAVA_API, 'api', 'api', artifacts, configurations, objectFactory, attributesFactory);
    }
}
