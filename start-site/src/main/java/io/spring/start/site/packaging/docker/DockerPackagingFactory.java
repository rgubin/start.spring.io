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

package io.spring.start.site.packaging.docker;

import io.spring.initializr.generator.packaging.Packaging;
import io.spring.initializr.generator.packaging.PackagingFactory;
import io.spring.initializr.generator.packaging.jar.JarPackaging;

/**
 * {@link PackagingFactory Factory} for {@link JarPackaging}.
 *
 */
class DockerPackagingFactory implements PackagingFactory {

	@Override
	public Packaging createPackaging(String id) {
		if (DockerPackaging.ID.equals(id)) {
			return new DockerPackaging();
		}
		return null;
	}

}
