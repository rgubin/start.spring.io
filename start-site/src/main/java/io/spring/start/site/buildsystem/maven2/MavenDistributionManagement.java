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

package io.spring.start.site.buildsystem.maven2;

import java.util.function.Consumer;

/**
 * Maven {@code <distributionManagement>} section.
 *
 * @author Joachim Pasquali
 * @author Stephane Nicoll
 */
public class MavenDistributionManagement {

	private final String downloadUrl;

	private final DeploymentRepository repository;

	private final DeploymentRepository snapshotRepository;

	private final Site site;

	private final Relocation relocation;

	MavenDistributionManagement(Builder builder) {
		this.downloadUrl = builder.downloadUrl;
		this.repository = builder.repository.build();
		this.snapshotRepository = builder.snapshotRepository.build();
		this.site = builder.site.build();
		this.relocation = builder.relocation.build();
	}

	public boolean isEmpty() {
		return this.downloadUrl == null && this.repository.isEmpty() && this.snapshotRepository.isEmpty()
				&& this.site.isEmpty() && this.relocation.isEmpty();
	}

	/**
	 * Return the URL where this project can be downloaded from.
	 * @return the URL of the project's download page
	 */
	public String getDownloadUrl() {
		return this.downloadUrl;
	}

	/**
	 * Return the information needed to deploy the artifacts generated by the project to a
	 * remote repository.
	 * @return the {@link DeploymentRepository} for released artifacts
	 */
	public DeploymentRepository getRepository() {
		return this.repository;
	}

	/**
	 * Return the information needed to deploy the snapshot artifacts generated by the
	 * project to a remote repository.
	 * @return the {@link DeploymentRepository} for snapshot artifacts
	 */
	public DeploymentRepository getSnapshotRepository() {
		return this.snapshotRepository;
	}

	/**
	 * Return the information needed for deploying the web site of the project.
	 * @return the {@link Site}
	 */
	public Site getSite() {
		return this.site;
	}

	/**
	 * Return the relocation information of the artifact if it has been moved to a new
	 * groupId and/or artifactId.
	 * @return the {@link Relocation}
	 */
	public Relocation getRelocation() {
		return this.relocation;
	}

	public static class Builder {

		private String downloadUrl;

		private DeploymentRepository.Builder repository = new DeploymentRepository.Builder();

		private DeploymentRepository.Builder snapshotRepository = new DeploymentRepository.Builder();

		private Site.Builder site = new Site.Builder();

		private Relocation.Builder relocation = new Relocation.Builder();

		/**
		 * Specify the URL where this project can be downloaded from.
		 * @param downloadUrl the URL of the project's download page
		 * @return this for method chaining
		 */
		public Builder downloadUrl(String downloadUrl) {
			this.downloadUrl = downloadUrl;
			return this;
		}

		/**
		 * Customize the {@code repository} using the specified consumer.
		 * @param repository a consumer of the current repository
		 * @return this for method chaining
		 */
		public Builder repository(Consumer<DeploymentRepository.Builder> repository) {
			repository.accept(this.repository);
			return this;
		}

		/**
		 * Customize the {@code snapshotRepository} using the specified consumer.
		 * @param snapshotRepository a consumer of the current snapshot repository
		 * @return this for method chaining
		 */
		public Builder snapshotRepository(Consumer<DeploymentRepository.Builder> snapshotRepository) {
			snapshotRepository.accept(this.snapshotRepository);
			return this;
		}

		/**
		 * Customize the {@code site} using the specified consumer.
		 * @param site a consumer of the current site
		 * @return this for method chaining
		 */
		public Builder site(Consumer<Site.Builder> site) {
			site.accept(this.site);
			return this;
		}

		/**
		 * Customize the {@code relocation} using the specified consumer.
		 * @param relocation a consumer of the current relocation
		 * @return this for method chaining
		 */
		public Builder relocation(Consumer<Relocation.Builder> relocation) {
			relocation.accept(this.relocation);
			return this;
		}

		/**
		 * Build a {@link io.spring.initializr.generator.buildsystem.maven.MavenDistributionManagement} with the current state of this
		 * builder.
		 * @return a {@link io.spring.initializr.generator.buildsystem.maven.MavenDistributionManagement}
		 */
		public MavenDistributionManagement build() {
			return new MavenDistributionManagement(this);
		}

	}

	/**
	 * Describe where to deploy artifacts.
	 */
	public static class DeploymentRepository {

		private final String id;

		private final String name;

		private final String url;

		private final String layout;

		private final Boolean uniqueVersion;

		DeploymentRepository(Builder builder) {
			this.id = builder.id;
			this.name = builder.name;
			this.url = builder.url;
			this.layout = builder.layout;
			this.uniqueVersion = builder.uniqueVersion;
		}

		public boolean isEmpty() {
			return this.id == null && this.name == null && this.url == null && this.layout == null
					&& this.uniqueVersion == null;
		}

		/**
		 * Return the identifier of the repository.
		 * @return the repository ID
		 */
		public String getId() {
			return this.id;
		}

		/**
		 * Return the name of the repository.
		 * @return the repository name
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Return the url of the repository to use to upload artifacts.
		 * @return the repository url
		 */
		public String getUrl() {
			return this.url;
		}

		/**
		 * Return the repository layout. Can be {@code default} or {@code legacy}.
		 * @return the repository layout
		 */
		public String getLayout() {
			return this.layout;
		}

		/**
		 * Return whether to assign snapshots a unique version comprised of the timestamp
		 * and build number, or to use the same version each time.
		 * @return {@code true} to assign each snapshot a unique version
		 */
		public Boolean getUniqueVersion() {
			return this.uniqueVersion;
		}

		public static class Builder {

			private String id;

			private String name;

			private String url;

			private String layout;

			private Boolean uniqueVersion;

			/**
			 * Set the id of the repository.
			 * @param id the identifier
			 * @return this for method chaining
			 */
			public Builder id(String id) {
				this.id = id;
				return this;
			}

			/**
			 * Set the name of the repository.
			 * @param name the name
			 * @return this for method chaining
			 */
			public Builder name(String name) {
				this.name = name;
				return this;
			}

			/**
			 * Set the url of the repository to use to upload artifacts. Specify both the
			 * location and the transport protocol used to transfer a built artifact to
			 * the repository.
			 * @param url the url
			 * @return this for method chaining
			 */
			public Builder url(String url) {
				this.url = url;
				return this;
			}

			/**
			 * Set the repository layout, can be {@code default} or {@code legacy}.
			 * @param layout the layout
			 * @return this for method chaining
			 */
			public Builder layout(String layout) {
				this.layout = layout;
				return this;
			}

			/**
			 * Set whether snapshots should be assigned a unique version comprised of the
			 * timestamp and build number.
			 * @param uniqueVersion {@code true} to use unique version for snapshots
			 * @return this for method chaining
			 */
			public Builder uniqueVersion(Boolean uniqueVersion) {
				this.uniqueVersion = uniqueVersion;
				return this;
			}

			/**
			 * Build a {@link DeploymentRepository} with the current state of this
			 * builder.
			 * @return a {@link DeploymentRepository}
			 */
			public DeploymentRepository build() {
				return new DeploymentRepository(this);
			}

		}

	}

	/**
	 * Information needed for deploying the web site of the project.
	 */
	public static class Site {

		private final String id;

		private final String name;

		private final String url;

		Site(Builder builder) {
			this.id = builder.id;
			this.name = builder.name;
			this.url = builder.url;
		}

		public boolean isEmpty() {
			return this.id == null && this.name == null && this.url == null;
		}

		/**
		 * Return the identifier of the repository.
		 * @return the repository ID
		 */
		public String getId() {
			return this.id;
		}

		/**
		 * Return the name of the repository.
		 * @return the repository name
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Return the url of the repository to use to upload the site.
		 * @return the repository url
		 */
		public String getUrl() {
			return this.url;
		}

		public static class Builder {

			private String id;

			private String name;

			private String url;

			/**
			 * Set the id of the repository.
			 * @param id the identifier
			 * @return this for method chaining
			 */
			public Builder id(String id) {
				this.id = id;
				return this;
			}

			/**
			 * Set the name of the repository.
			 * @param name the name
			 * @return this for method chaining
			 */
			public Builder name(String name) {
				this.name = name;
				return this;
			}

			/**
			 * Set the url of the repository to use to upload the site. Specify both the
			 * location and the transport protocol to use.
			 * @param url the url
			 * @return this for method chaining
			 */
			public Builder url(String url) {
				this.url = url;
				return this;
			}

			/**
			 * Build a {@link Site} with the current state of this builder.
			 * @return a {@link Site}
			 */
			public Site build() {
				return new Site(this);
			}

		}

	}

	/**
	 * Relocation information of the artifact if it has been moved to a new groupId and/or
	 * artifactId.
	 */
	public static class Relocation {

		private final String groupId;

		private final String artifactId;

		private final String version;

		private final String message;

		Relocation(Builder builder) {
			this.groupId = builder.groupId;
			this.artifactId = builder.artifactId;
			this.version = builder.version;
			this.message = builder.message;
		}

		public boolean isEmpty() {
			return this.groupId == null && this.artifactId == null && this.version == null && this.message == null;
		}

		/**
		 * Return the new group ID of the dependency.
		 * @return the relocated group ID
		 */
		public String getGroupId() {
			return this.groupId;
		}

		/**
		 * Return the new artifact ID of the dependency.
		 * @return the relocated artifact ID
		 */
		public String getArtifactId() {
			return this.artifactId;
		}

		/**
		 * Return the new version of the dependency.
		 * @return the relocated version
		 */
		public String getVersion() {
			return this.version;
		}

		/**
		 * Return a message that provides more details about the relocation.
		 * @return the relocation message
		 */
		public String getMessage() {
			return this.message;
		}

		public static class Builder {

			private String groupId;

			private String artifactId;

			private String version;

			private String message;

			/**
			 * Specify the new group ID of the dependency.
			 * @param groupId the new group ID of the dependency
			 * @return this for method chaining
			 */
			public Builder groupId(String groupId) {
				this.groupId = groupId;
				return this;
			}

			/**
			 * Specify the new artifact ID of the dependency.
			 * @param artifactId the new artifact ID of the dependency
			 * @return this for method chaining
			 */
			public Builder artifactId(String artifactId) {
				this.artifactId = artifactId;
				return this;
			}

			/**
			 * Specify the new version of the dependency.
			 * @param version the new version of the dependency
			 * @return this for method chaining
			 */
			public Builder version(String version) {
				this.version = version;
				return this;
			}

			/**
			 * Specify a message that provides more details about the relocation.
			 * @param message the relocation message
			 * @return this for method chaining
			 */
			public Builder message(String message) {
				this.message = message;
				return this;
			}

			/**
			 * Build a {@link Relocation} with the current state of this builder.
			 * @return a {@link Relocation}
			 */
			public Relocation build() {
				return new Relocation(this);
			}

		}

	}

}
