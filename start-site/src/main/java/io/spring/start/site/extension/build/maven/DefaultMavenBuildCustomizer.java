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

import io.spring.initializr.generator.buildsystem.BillOfMaterials;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.initializr.generator.version.VersionProperty;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Maven;
import io.spring.initializr.metadata.InitializrConfiguration.Env.Maven.ParentPom;
import io.spring.initializr.metadata.InitializrMetadata;
import io.spring.initializr.metadata.support.MetadataBuildItemMapper;
import io.spring.start.site.buildsystem.maven2.MavenBuild;
import io.spring.start.site.buildsystem.maven2.MavenProfile;
import io.spring.start.site.buildsystem.maven2.MavenPlugin;

import java.util.function.Consumer;

/**
 * The default {@link Maven} {@link BuildCustomizer}.
 *
 * @author Stephane Nicoll
 */
public class DefaultMavenBuildCustomizer implements BuildCustomizer<MavenBuild> {

	private final ProjectDescription description;

	private final InitializrMetadata metadata;

	public DefaultMavenBuildCustomizer(ProjectDescription description, InitializrMetadata metadata) {
		this.description = description;
		this.metadata = metadata;
	}

	@Override
	public void customize(MavenBuild build) {
		build.settings().name(this.description.getName()).description(this.description.getDescription());
		build.properties().property("java.version", this.description.getLanguage().jvmVersion());
		build.plugins().add("org.springframework.boot", "spring-boot-maven-plugin");

		Maven maven = this.metadata.getConfiguration().getEnv().getMaven();
		String springBootVersion = this.description.getPlatformVersion().toString();
		ParentPom parentPom = maven.resolveParentPom(springBootVersion);
		if (parentPom.isIncludeSpringBootBom()) {
			String versionProperty = "spring-boot.version";
			BillOfMaterials springBootBom = MetadataBuildItemMapper
					.toBom(this.metadata.createSpringBootBom(springBootVersion, versionProperty));
			if (!hasBom(build, springBootBom)) {
				build.properties().version(VersionProperty.of(versionProperty, true), springBootVersion);
				build.boms().add("spring-boot", springBootBom);
			}
		}
		if (!maven.isSpringBootStarterParent(parentPom)) {
			build.properties().property("project.build.sourceEncoding", "UTF-8")
					.property("project.reporting.outputEncoding", "UTF-8");
		}
		build.settings().parent(parentPom.getGroupId(), parentPom.getArtifactId(), parentPom.getVersion());

		//tests

		build.plugins().add("org.apache.maven.plugins", "maven-failsafe-plugin", failsafePlugin -> {
			failsafePlugin.version("2.22.2");
			failsafePlugin.configuration(config ->
					config.add("argLine", "-Ddb-host=${docker.container.db.ip} -Duse-datasource=true"));
		});
		build.plugins().add("org.apache.maven.plugins", "maven-surefire-plugin", surefirePlugin -> {
			surefirePlugin.version("2.22.2");
			surefirePlugin.configuration(config ->
					config.configure("excludes", excludes -> {
						excludes.add("exclude", "**/*Tests.java");
					}));
		});

		Consumer<MavenPlugin.Builder> dockerPluginConsumer = dockerPlugin -> {
			dockerPlugin.version("0.33.0");
			dockerPlugin.configuration(config ->
					config.configure("images", images -> {
						images.configure("image", image -> {
							image.add("name", "postgres:10");
							image.add("alias", "db");
							image.configure("run", run -> {
								run.add("namingStrategy", "alias");
								run.configure("env", env -> {
									env.add("POSTGRES_DB", build.getSettings().getName() + "-db");
									env.add("POSTGRES_USER", "porta");
									env.add("POSTGRES_PASSWORD", "porta");
								});
								run.configure("ports", port -> port.add("port", "5432:5432"));
								run.configure("wait", wait -> {
									wait.configure("tcp", tcp ->
											tcp.configure("ports",
													port -> port.add("port", "5432")));
									wait.add("time", "20000");
								});
								run.configure("log", log -> log.add("color", "green"));
							});
						});
					}));
			dockerPlugin.execution("start", execution -> execution.phase("pre-integration-test").goal("stop").goal("start"));
			dockerPlugin.execution("stop", execution -> execution.phase("post-integration-test").goal("stop"));
		};

		MavenBuild profileBuild = new MavenBuild();
		profileBuild.plugins().add("io.fabric8", "docker-maven-plugin", dockerPluginConsumer);
		MavenProfile profile = new MavenProfile.Builder()
				.id("pg-docker")
				.activation(a->a.activeByDefault(false))
				.mavenBuild(profileBuild)
				.build();
		build.buildProfiles().add(profile);
	}

	private boolean hasBom(MavenBuild build, BillOfMaterials bom) {
		return build.boms().items().anyMatch((candidate) -> candidate.getGroupId().equals(bom.getGroupId())
				&& candidate.getArtifactId().equals(bom.getArtifactId()));
	}
}
