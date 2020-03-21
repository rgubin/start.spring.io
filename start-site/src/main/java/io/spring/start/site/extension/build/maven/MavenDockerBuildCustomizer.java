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

import io.spring.initializr.generator.buildsystem.maven.MavenBuild;
import io.spring.initializr.generator.packaging.jar.JarPackaging;
import io.spring.initializr.generator.spring.build.BuildCustomizer;

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
        build.plugins().add("com.spotify", "dockerfile-maven-plugin", dockerPlugin -> {
            dockerPlugin.version("1.4.8");
            dockerPlugin.execution("package", execution -> execution.goal("build").goal("tag").phase("package"));
            dockerPlugin.execution("deploy", execution -> execution.goal("push").phase("deploy"));
            String jarFile = "target/${project.artifactId}-${project.version}.jar";
            dockerPlugin.configuration(config -> config.add("repository", "${project.artifactId}"));
            dockerPlugin.configuration(config -> config.add("tag", "${project.version}"));
            dockerPlugin.configuration(config ->
                    config.configure("buildArgs", args -> args.add("JAR_FILE", jarFile)));
        });
    }
}
