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

package io.spring.start.site.extension.code.java.realpage;

import io.spring.initializr.generator.io.template.TemplateRenderer;
import io.spring.initializr.generator.language.SourceStructure;
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.contributor.ProjectContributor;
import io.spring.initializr.generator.project.contributor.SingleResourceProjectContributor;
import io.spring.start.site.packaging.docker.DockerPackaging;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JavaTemplatesContributor implements ProjectContributor {

	private final TemplateRenderer templateRenderer;

	private final ProjectDescription description;

	JavaTemplatesContributor(TemplateRenderer templateRenderer, ProjectDescription description) {
		this.templateRenderer = templateRenderer;
		this.description = description;
	}

	private void write(File target, String templateName, Map<String, Object> model) throws IOException {
		String body = templateRenderer.render(templateName, model);
		writeText(target, body);
	}

	private void writeText(File target, String body) {
		try (OutputStream stream = new FileOutputStream(target)) {
			StreamUtils.copy(body, Charset.forName("UTF-8"), stream);
		}
		catch (Exception e) {
			throw new IllegalStateException("Cannot write file " + target, e);
		}
	}

	@Override
	public void contribute(Path projectRoot) throws IOException {
		SourceStructure mainSource = description.getBuildSystem().getMainSource(projectRoot, description.getLanguage());
		Map<String, Object> model = resolveModel();

		String projectTemplateName = "starter2"; // now it's hardcoded
		contributeProjectTemplates(projectTemplateName, projectRoot, mainSource, model);
	}

	private void contributeProjectTemplates(String projectTemplateName, Path projectRoot, SourceStructure mainSource,
			Map<String, Object> model) throws IOException {
		if (Boolean.TRUE.equals(model.get("useSwagger2"))) {
			write(new File(mainSource.createSourceFile(model.get("packageName") + ".configuration", "SwaggerConfig")
					.toString()), projectTemplateName + "/src/main/java/configuration/SwaggerConfig.java", model);
			write(new File(
					mainSource.createSourceFile(model.get("packageName").toString(), "HomeController").toString()),
					projectTemplateName + "/src/main/java/HomeController.java", model);
		}
		if (Boolean.TRUE.equals(model.get("useSecurity")) && Boolean.TRUE.equals(model.get("useJwt"))) {
			write(new File(mainSource.createSourceFile(model.get("packageName") + ".configuration", "SecurityConfig")
					.toString()), projectTemplateName + "/src/main/java/configuration/SecurityConfig.java", model);
			write(new File(
					mainSource.createSourceFile(model.get("packageName") + ".security", "JwtConfigurer").toString()),
					projectTemplateName + "/src/main/java/security/JwtConfigurer.java", model);
			write(new File(
					mainSource.createSourceFile(model.get("packageName") + ".security", "JwtTokenFilter").toString()),
					projectTemplateName + "/src/main/java/security/JwtTokenFilter.java", model);
			write(new File(
					mainSource.createSourceFile(model.get("packageName") + ".security", "JwtTokenProvider").toString()),
					projectTemplateName + "/src/main/java/security/JwtTokenProvider.java", model);
			write(new File(
					mainSource.createSourceFile(model.get("packageName") + ".security", "AppMDCFilter").toString()),
					projectTemplateName + "/src/main/java/security/AppMDCFilter.java", model);
		}
		write(new File(mainSource.createSourceFile(model.get("packageName") + ".configuration", "ApplicationConfig")
				.toString()), projectTemplateName + "/src/main/java/configuration/ApplicationConfig.java", model);
		try {
			copyCommonSources(projectTemplateName + "/src/main/java/api", mainSource, model,
					projectTemplateName + ".src.main.java");
			copyCommonSources(projectTemplateName + "/src/test/java",
					description.getBuildSystem().getTestSource(projectRoot, description.getLanguage()), model,
					projectTemplateName + ".src.test.java");
		}
		catch (URISyntaxException e) {
			e.printStackTrace();
		}
		SingleResourceProjectContributor contributor = new SingleResourceProjectContributor(
				"src/main/resources/keystore.p12", "classpath:configuration/keystore.p12");
		contributor.contribute(projectRoot);

		if (Boolean.TRUE.equals(model.get("useLiquibase"))) {
			write(new File(mainSource.createSourceFile(model.get("packageName") + ".configuration", "DataSourceConfig")
					.toString()), projectTemplateName + "/src/main/java/configuration/DataSourceConfig.java", model);
			SingleResourceProjectContributor contributorDb = new SingleResourceProjectContributor(
					"src/main/resources/db/changelog/db.changelog-master.yaml",
					"classpath:configuration/db.changelog-master.yaml");
			contributorDb.contribute(projectRoot);
			SingleResourceProjectContributor contributorMg = new SingleResourceProjectContributor(
					"src/main/resources/db/changelog/migration/FillCompanies.sql",
					"classpath:configuration/FillCompanies.sql");
			contributorMg.contribute(projectRoot);
		}

		SingleResourceProjectContributor contributorStatic = new SingleResourceProjectContributor(
				"src/main/resources/static/oauth2-redirect.html", "classpath:configuration/oauth2-redirect.html");
		contributorStatic.contribute(projectRoot);

		write(new File(mainSource.createResourceFile("", "logback-spring.xml").toString()),
				projectTemplateName + "/src/main/resources/logback-spring.xml", model);
		write(new File(mainSource.createResourceFile("", "application.yml").toString()),
				projectTemplateName + "/src/main/resources/application.yml", model);

		write(new File(mainSource.createResourceFile("api", "swagger.yml").toString()),
				projectTemplateName + "/src/main/resources/api/swagger.yml", model);

		write(new File(projectRoot.toString(), "README.md"), projectTemplateName + "/README.md", model);

		if (DockerPackaging.ID.equals(description.getPackaging().id())) {
			write(new File(projectRoot.toString(), "Dockerfile"), projectTemplateName + "/Dockerfile", model);
			File execFile = new File(projectRoot.toString(), "run.sh");
			Files.createFile(execFile.toPath());
			execFile.setExecutable(true);
			write(execFile, projectTemplateName + "/run.sh", model);
		}
	}

	private void copyCommonSources(String prefix, SourceStructure mainSource, Map<String, Object> model, String replace)
			throws IOException, URISyntaxException {
		for (URL u : ResourcesScanner.getResourceURLs()) {
			String path = u.getFile();
			if (path.endsWith(".mustache") && path.contains(prefix)) {
				String[] split = path.split(prefix);
				File f = new File(path);
				String fullName = f.getName();
				String tmpl = fullName.substring(0, fullName.indexOf('.'));
				String last = split[1];
				String packageNameSuffix = last.substring(0, last.lastIndexOf("/")).replaceAll("/", ".");
				String packageName = model.get("packageName") + prefix.replaceAll("/", ".").replace(replace, "");
				String lastName = last.replaceAll(".mustache", "");
				write(new File(mainSource.createSourceFile(packageName + packageNameSuffix, tmpl).toString()),
						prefix + lastName, model);
			}
		}
	}

	private Map<String, Object> resolveModel() {
		Map<String, Object> model = new HashMap<>();
		model.put("name", description.getName());
		model.put("version", description.getVersion());
		model.put("packageName", description.getPackageName());
		model.put("applicationName", description.getApplicationName());
		model.put("useSwagger2", description.getRequestedDependencies().containsKey("swagger2"));
		model.put("useSpringWeb", description.getRequestedDependencies().containsKey("web"));
		model.put("useSecurity", description.getRequestedDependencies().containsKey("security"));
		model.put("useJwt", description.getRequestedDependencies().containsKey("java-jwt"));
		model.put("useLogging", description.getRequestedDependencies().containsKey("lombok"));
		model.put("useLiquibase", description.getRequestedDependencies().containsKey("liquibase"));
		model.put("useActuator", description.getRequestedDependencies().containsKey("actuator"));
		return model;
	}

}
