/*
 * Copyright 2012-2020 the original author or authors.
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
import io.spring.initializr.generator.project.ProjectDescription;
import io.spring.initializr.generator.project.ProjectGenerationConfiguration;
import io.spring.initializr.metadata.InitializrMetadata;
import org.springframework.context.annotation.Bean;

@ProjectGenerationConfiguration
public class JavaSourceCodeGenerationConfiguration {

	private final InitializrMetadata metadata;

	private final ProjectDescription description;

	private final TemplateRenderer templateRenderer;

	public JavaSourceCodeGenerationConfiguration(InitializrMetadata metadata, ProjectDescription description,
			TemplateRenderer templateRenderer) {
		this.metadata = metadata;
		this.description = description;
		this.templateRenderer = templateRenderer;
	}

	@Bean
	public JavaTemplatesContributor javaTemplatesContributor() {
		return new JavaTemplatesContributor(templateRenderer, description);
	}

}
