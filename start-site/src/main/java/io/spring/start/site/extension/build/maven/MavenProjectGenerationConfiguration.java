/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.spring.start.site.extension.build.maven;

import io.spring.initializr.generator.buildsystem.Build;
import io.spring.initializr.generator.buildsystem.BuildItemResolver;
import io.spring.initializr.generator.buildsystem.Dependency;
import io.spring.initializr.generator.buildsystem.DependencyScope;
import io.spring.initializr.generator.condition.ConditionalOnBuildSystem;
import io.spring.initializr.generator.condition.ConditionalOnPackaging;
import io.spring.initializr.generator.condition.ConditionalOnPlatformVersion;
import io.spring.initializr.generator.io.IndentingWriterFactory;
import io.spring.initializr.generator.packaging.war.WarPackaging;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.spring.util.LambdaSafe;
import io.spring.initializr.generator.version.VersionReference;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.start.site.buildsystem.maven2.MavenBuild;
import io.spring.start.site.buildsystem.maven2.MavenBuildSystem;
import io.spring.start.site.packaging.docker.DockerPackaging;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link ProjectGenerationConfiguration} for generation of projects that depend on Maven.
 *
 * @author Stephane Nicoll
 */
@ProjectGenerationConfiguration
@ConditionalOnBuildSystem(MavenBuildSystem.ID)
class MavenProjectGenerationConfiguration {

	private final ProjectDescription description;

	public MavenProjectGenerationConfiguration(ProjectDescription description) {
		this.description = description;
	}

	@Bean
	public DefaultMavenBuildCustomizer initializrMetadataMaven2BuildCustomizer(ProjectDescription description,
																			  InitializrMetadata metadata) {
		return new DefaultMavenBuildCustomizer(description, metadata);
	}

	@Bean
	MavenBuildSystemHelpDocumentCustomizer mavenBuildSystemHelpDocumentCustomizer(ProjectDescription description) {
		return new MavenBuildSystemHelpDocumentCustomizer(description);
	}

	@Bean
	@ConditionalOnPackaging(DockerPackaging.ID)
	public MavenDockerBuildCustomizer mavenDockerBuildCustomizer() {
		return new MavenDockerBuildCustomizer();
	}

	@Bean
	public MavenWrapperContributor mavenWrapperContributor() {
		return new MavenWrapperContributor();
	}

	@Bean
	public MavenBuild mavenBuild(ObjectProvider<BuildItemResolver> buildItemResolver,
								 ObjectProvider<BuildCustomizer<?>> buildCustomizers) {
		return createBuild(buildItemResolver.getIfAvailable(),
				buildCustomizers.orderedStream().collect(Collectors.toList()));
	}

	@SuppressWarnings("unchecked")
	private MavenBuild createBuild(BuildItemResolver buildItemResolver, List<BuildCustomizer<?>> buildCustomizers) {
		MavenBuild build = (buildItemResolver != null) ? new MavenBuild(buildItemResolver) : new MavenBuild();
		build.settings().name(description.getName());
		LambdaSafe.callbacks(BuildCustomizer.class, buildCustomizers, build)
				.invoke((customizer) -> customizer.customize(build));
		return build;
	}

	@Bean
	public MavenBuildProjectContributor mavenBuildProjectContributor(MavenBuild build,
																	 IndentingWriterFactory indentingWriterFactory) {
		return new MavenBuildProjectContributor(build, indentingWriterFactory);
	}

	@Bean
	@ConditionalOnPlatformVersion("2.2.0.M5")
	public BuildCustomizer<Build> junitJupiterAddTestStarterContributor() {
		return (build) -> build.dependencies().add("test-j5",
				Dependency.withCoordinates("org.junit.jupiter", "junit-jupiter-engine")
						.scope(DependencyScope.TEST_COMPILE));
	}

	@Bean
	public BuildCustomizer<Build> restAssuredContributor() {
		return (build) -> build.dependencies().add("restassured",
				Dependency.withCoordinates("io.rest-assured", "rest-assured")
						.scope(DependencyScope.TEST_COMPILE)
						.version(VersionReference.ofValue("3.0.1")));
	}

	@Bean
	@ConditionalOnPackaging(WarPackaging.ID)
	public BuildCustomizer<MavenBuild> mavenWarPackagingConfigurer() {
		return (build) -> build.settings().packaging("war");
	}
}
