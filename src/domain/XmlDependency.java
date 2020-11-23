package domain;

import com.intellij.psi.xml.XmlText;

/**
 *
 */
public class XmlDependency {
    private final Dependency dependency;
    private final XmlText versionXmlText;

    private XmlDependency(final Builder builder) {
        this.dependency = builder.dependency;
        this.versionXmlText = builder.versionXmlText;
    }

    /**
     *
     * @return
     */
    public Dependency getDependency() {
        return dependency;
    }

    /**
     *
     * @return
     */
    public XmlText getVersionXmlText() {
        return versionXmlText;
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
        private Dependency dependency;
        private XmlText versionXmlText;

        private Builder() {
        }

        /**
         *
         * @param dependency
         * @return
         */
        public Builder withDependency(final Dependency dependency) {
            this.dependency = dependency;
            return this;
        }

        /**
         *
         * @param versionXmlText
         * @return
         */
        public Builder withVersionXmlText(final XmlText versionXmlText) {
            this.versionXmlText = versionXmlText;
            return this;
        }

        /**
         *
         * @return
         */
        public XmlDependency build() {
            return new XmlDependency(this);
        }
    }
}
