package provider;

import com.intellij.psi.xml.XmlTag;
import domain.Dependency;

import java.util.Arrays;

/**
 *
 */
public class FreeVersionProvider {
    private static final String VERSION_SUFFIX = ".version";
    private static final char DASH = '-';
    private static final int MAX_ATTEMPTS = 20;

    private final PropertiesProvider propertiesProvider = new PropertiesProvider();

    /**
     *
     * @param project
     * @param dependency
     * @return
     */
    public String getFreeVersion(final XmlTag project, final Dependency dependency) {
        final var properties = propertiesProvider.provide(project);
        String candidate = dependency.getArtifactId() + VERSION_SUFFIX;
        if (properties != null) {
            if (isPropertyInUse(properties, candidate)) {
                candidate = fallbackWithFullName(properties, dependency);
            }
        }
        return candidate;
    }

    private String fallbackWithFullName(final XmlTag properties, final Dependency dependency) {
        String candidate = dependency.getGroupId() + DASH + dependency.getArtifactId() + VERSION_SUFFIX;
        int attempt = 1;
        while (isPropertyInUse(properties, candidate) && attempt < MAX_ATTEMPTS) {
            candidate = dependency.getGroupId() + DASH + dependency.getArtifactId() + DASH + (attempt+1) + VERSION_SUFFIX;
            attempt++;
        }
        if (isPropertyInUse(properties, candidate)) {
            candidate = null;
        }
        return candidate;
    }

    private boolean isPropertyInUse(final XmlTag properties, final String property) {
        final var name = property.toLowerCase();
        return Arrays.stream(properties.getChildren())
                .filter(XmlTag.class::isInstance)
                .map(XmlTag.class::cast)
                .anyMatch(xmlTag -> name.equals(xmlTag.getName().toLowerCase()));
    }
}
