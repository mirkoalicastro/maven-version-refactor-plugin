package updater;

import com.intellij.psi.xml.XmlTag;
import domain.Pom;
import provider.PropertiesProvider;

import java.util.Optional;

import static domain.constant.XmlNodeName.PROPERTIES;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 *
 */
public class PropertiesUpdater {
    private final PropertiesProvider propertiesProvider = new PropertiesProvider();

    /**
     *
     * @param pom
     * @param freeVersion
     */
    public void addVersionToProperties(final Pom pom, final String freeVersion) {
        var properties = getPropertiesOrCreateIfMissing(pom.getProject());
        addProperty(properties, freeVersion, getVersion(pom), false);
    }

    private XmlTag getPropertiesOrCreateIfMissing(final XmlTag project) {
        return Optional.ofNullable(propertiesProvider.provide(project))
                .orElseGet(() -> createProperties(project));
    }

    private String getVersion(final Pom pom) {
        return pom.getXmlDependency().getDependency().getVersion();
    }

    private XmlTag createProperties(final XmlTag project) {
        addProperty(project, PROPERTIES.getXmlName(), EMPTY, true);
        return propertiesProvider.provide(project);
    }

    private void addProperty(final XmlTag parent, final String node, final String value, final boolean first) {
        final var childTag = parent.createChildTag(node, parent.getNamespace(), value, true);
        parent.addSubTag(childTag, first);
    }
}
