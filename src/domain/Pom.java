package domain;

import com.intellij.psi.xml.XmlTag;

/**
 *
 */
public class Pom {
    private static final String MAVEN_NAMESPACE = "^(http://maven\\.apache\\.org).*$";

    private final String namespace;
    private final XmlTag project;
    private final XmlDependency xmlDependency;

    private Pom(final Builder builder) {
        this.namespace = builder.namespace;
        this.xmlDependency = builder.xmlDependency;
        this.project = builder.project;
    }

    /**
     *
     * @return
     */
    public XmlDependency getXmlDependency() {
        return xmlDependency;
    }

    /**
     *
     * @return
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     *
     * @return
     */
    public XmlTag getProject() {
        return project;
    }

    /**
     *
     * @return
     */
    public boolean hasValidNamespace() {
        return namespace.matches(MAVEN_NAMESPACE);
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
     */
    public static final class Builder {
        private XmlTag project;
        private String namespace;
        private XmlDependency xmlDependency;

        private Builder() {
        }

        /**
         *
         * @param project
         * @return
         */
        public Builder withProject(final XmlTag project) {
            this.project = project;
            return this;
        }

        /**
         *
         * @param namespace
         * @return
         */
        public Builder withNamespace(final String namespace) {
            this.namespace = namespace;
            return this;
        }

        /**
         *
         * @param xmlDependency
         * @return
         */
        public Builder withXmlDependency(final XmlDependency xmlDependency) {
            this.xmlDependency = xmlDependency;
            return this;
        }

        /**
         *
         * @return
         */
        public Pom build() {
            return new Pom(this);
        }
    }
}
