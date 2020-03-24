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

import java.util.ArrayList;
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
        build.plugins().add("org.apache.maven.plugins", "maven-deploy-plugin", mavenDeployPlugin ->
                mavenDeployPlugin.configuration(config ->
                        config.add("skip", "true")));
        build.plugins().add("com.spotify", "dockerfile-maven-plugin", dockerFilePlugin -> {
            dockerFilePlugin.version("1.4.8");
            dockerFilePlugin.execution("package", execution -> execution.goal("build").goal("tag").phase("package"));
            dockerFilePlugin.execution("deploy", execution -> execution.goal("push").phase("deploy"));
            String jarFile = "target/${project.artifactId}-${project.version}.jar";
            dockerFilePlugin.configuration(config -> config.add("repository", "${project.artifactId}"));
            dockerFilePlugin.configuration(config -> config.add("tag", "${project.version}"));
            dockerFilePlugin.configuration(config ->
                    config.configure("buildArgs", args -> args.add("JAR_FILE", jarFile)));
        });

        build.plugins().add("io.fabric8", "docker-maven-plugin", dockerPlugin ->
                dockerPlugin.configuration(config ->
                        config.configure("images", images -> {
                           images.addConfigure("image", image -> {
                                image.add("name", "postgres:10");
                                image.add("alias", "db");
                                image.configure("run", naming -> {
                                    naming.add("namingStrategy", "alias");
                                    naming.configure("env", env -> {
                                        env.add("POSTGRES_DB", build.getSettings().getName() + "-db");
                                        env.add("POSTGRES_USER", "porta");
                                        env.add("POSTGRES_PASSWORD", "porta");
                                    });
                                });

                            });
                           images.addConfigure("image", image -> {
                                image.add("name", "postgres:110");
                                image.add("alias", "db");

                            });
                        })));

    }
}
