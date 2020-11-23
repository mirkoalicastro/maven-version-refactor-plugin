package domain;

/**
 *
 */
public class Dependency {
    private final String groupId;
    private final String artifactId;
    private final String version;

    private Dependency(final Builder builder) {
        this.artifactId = builder.artifactId;
        this.groupId = builder.groupId;
        this.version = builder.version;
    }

    /**
     *
     * @return
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     *
     * @return
     */
    public String getVersion() {
        return version;
    }

    /**
     *
     * @return
     */
    public String getArtifactId() {
        return artifactId;
    }

    /**
     * @return
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     *
     */
    public static final class Builder {
        private String groupId;
        private String artifactId;
        private String version;

        private Builder() {
        }

        /**
         * @param groupId
         * @return
         */
        public Builder withGroupId(final String groupId) {
            this.groupId = groupId;
            return this;
        }

        /**
         * @param artifactId
         * @return
         */
        public Builder withArtifactId(final String artifactId) {
            this.artifactId = artifactId;
            return this;
        }

        /**
         * @param version
         * @return
         */
        public Builder withVersion(final String version) {
            this.version = version;
            return this;
        }

        /**
         * @return
         */
        public Dependency build() {
            return new Dependency(this);
        }
    }
}
