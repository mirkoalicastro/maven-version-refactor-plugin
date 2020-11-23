package domain.constant;

/**
 *
 */
public enum XmlNodeName {
    PROPERTIES("properties"), DEPENDENCY("dependency"), VERSION("version"),
    GROUP_ID("groupId"), ARTIFACT_ID("artifactId"), PROJECT("project");

    private final String xmlName;

    XmlNodeName(final String xmlName) {
        this.xmlName = xmlName;
    }

    /**
     *
     * @return
     */
    public String getXmlName() {
        return xmlName;
    }
}
