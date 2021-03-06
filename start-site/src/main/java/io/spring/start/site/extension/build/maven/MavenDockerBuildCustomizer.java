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

import io.spring.initializr.generator.packaging.jar.JarPackaging;
import io.spring.initializr.generator.spring.build.BuildCustomizer;
import io.spring.start.site.buildsystem.maven2.MavenBuild;
import io.spring.start.site.buildsystem.maven2.MavenPlugin;

import java.util.function.Consumer;

/**
 * A {@link BuildCustomizer} that automatically adds {@code spring-security-test} when
 * Spring Security is selected.
 */
public class MavenDockerBuildCustomizer implements BuildCustomizer<MavenBuild> {

	@Override
	public void customize(MavenBuild build) {
		build.settings().packaging(JarPackaging.ID);
		build.plugins().add("org.springframework.boot", "spring-boot-maven-plugin");
		build.plugins().add("org.apache.maven.plugins", "maven-deploy-plugin",
				mavenDeployPlugin -> mavenDeployPlugin.configuration(config -> config.add("skip", "true")));
		build.plugins().add("com.spotify", "dockerfile-maven-plugin", dockerFilePlugin -> {
			dockerFilePlugin.version("1.4.8");
			dockerFilePlugin.execution("package", execution -> execution.goal("build").goal("tag").phase("package"));
			dockerFilePlugin.execution("deploy", execution -> execution.goal("push").phase("deploy"));
			String jarFile = "target/${project.artifactId}-${project.version}.jar";
			dockerFilePlugin.configuration(config -> config.add("repository", "${project.artifactId}"));
			dockerFilePlugin.configuration(config -> config.add("tag", "${project.version}"));
			dockerFilePlugin
					.configuration(config -> config.configure("buildArgs", args -> args.add("JAR_FILE", jarFile)));
		});

		Consumer<MavenPlugin.Builder> dockerPluginConsumer = dockerPlugin -> {
			dockerPlugin.version("0.33.0");
			dockerPlugin.configuration(config -> config.configure("images",
					images -> images.add("image", new MavenPlugin.ConfigurationBuilder().add("name", "postgres:10")
							.add("alias", "db").configure("run", run -> {
								run.add("namingStrategy", "alias");
								run.configure("env", env -> {
									env.add("POSTGRES_DB", build.getSettings().getName() + "-db");
									env.add("POSTGRES_USER", "porta");
									env.add("POSTGRES_PASSWORD", "porta");
								});
								run.configure("wait", wait -> {
									wait.configure("tcp",
											tcp -> tcp.configure("ports", port -> port.add("port", "5432")));
									wait.add("time", "20000");
								});
								run.configure("log", log -> log.add("color", "cyan"));
							}))));
			dockerPlugin.execution("start",
					execution -> execution.phase("pre-integration-test").goal("stop").goal("start"));
			dockerPlugin.execution("stop", execution -> execution.phase("post-integration-test").goal("stop"));
		};
		build.plugins().add("io.fabric8", "docker-maven-plugin", dockerPluginConsumer);
	}

}
