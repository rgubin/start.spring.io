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
 * A {@code <developer>} in a Maven pom.
 *
 * @author Roman I. Gubin
 */
public class MavenBuildProfile {

    private final String id;

    private final MavenBuild mavenBuild;

    private final Activation activation;

    MavenBuildProfile(Builder builder) {
        this.id = builder.id;
        this.mavenBuild = builder.mavenBuild;
        this.activation = (builder.activation == null) ? null : builder.activation.build();
    }

    public boolean isEmpty() {
        return id == null;
    }

    /**
     * Return the ID of the developer.
     *
     * @return the ID
     */
    public String getId() {
        return this.id;
    }

    public MavenBuild getMavenBuild() {
        return this.mavenBuild;
    }

    public Activation getActivation() {
        return this.activation;
    }

    public static class Builder {
        private String id;
        private MavenBuild mavenBuild = new MavenBuild();
        private Activation.Builder activation = new Activation.Builder();

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder mavenBuild(MavenBuild build) {
            this.mavenBuild = build;
            return this;
        }

        public Builder activation(Activation.Builder activationBuilder) {
            this.activation = activationBuilder;
            return this;
        }

		public Builder activation(Consumer<Activation.Builder> activation) {
			activation.accept(this.activation);
			return this;
		}

        public MavenBuildProfile build() {
            return new MavenBuildProfile(this);
        }

    }

    public static final class Activation {
        private final Boolean activeByDefault;
        private final String jdk;
        private final ActivationOS os;

        Activation(Builder builder) {
            this.activeByDefault = builder.activeByDefault;
            this.jdk = builder.jdk;
            this.os = (builder.os == null) ? null : builder.os.build();
        }

        public boolean isEmpty() {
            return !(activeByDefault != null || jdk != null || os != null);
        }

        public Boolean isActiveByDefault() {
            return activeByDefault;
        }

        public String getJdk() {
            return jdk;
        }

        public ActivationOS getOs() {
            return os;
        }

        public static class Builder {
            private Boolean activeByDefault;
            private String jdk;
            private ActivationOS.Builder os = new ActivationOS.Builder();

            public Builder activeByDefault(boolean activeByDefault) {
                this.activeByDefault = activeByDefault;
                return this;
            }

            public Builder jdk(String jdk) {
                this.jdk = jdk;
                return this;
            }

            public Builder activationOsBuilder(ActivationOS.Builder builder) {
                this.os = builder;
                return this;
            }

            public Builder os(Consumer<ActivationOS.Builder> os) {
                os.accept(this.os);
                return this;
            }

            public Activation build() {
                return new Activation(this);
            }
        }
    }

    public static final class ActivationOS {
        private String name;
        private String family;
        private String arch;
        private String version;

        public ActivationOS(ActivationOS.Builder builder) {
            this.name = builder.name;
            this.family = builder.family;
            this.arch = builder.arch;
            this.version = builder.version;
        }


        public boolean isEmpty() {
            return !(name != null || family != null || arch != null || version != null);
        }

        public String getName() {
            return name;
        }

        public String getFamily() {
            return family;
        }

        public String getArch() {
            return arch;
        }

        public String getVersion() {
            return version;
        }

        public static class Builder {

            public String name;
            public String family;
            public String arch;
            public String version;

            public Builder name(String name) {
                this.name = name;
                return this;
            }

            public Builder family(String family) {
                this.family = family;
                return this;
            }

            public Builder arch(String arch) {
                this.arch = arch;
                return this;
            }

            public Builder version(String version) {
                this.version = version;
                return this;
            }

            public ActivationOS build() {
                return new ActivationOS(this);
            }
        }
    }
}
