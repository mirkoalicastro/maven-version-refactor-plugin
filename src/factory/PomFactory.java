package factory;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlElement;
import com.intellij.psi.xml.XmlTag;
import com.intellij.psi.xml.XmlText;
import com.intellij.psi.xml.XmlToken;
import domain.Dependency;
import domain.XmlDependency;
import domain.Pom;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static domain.constant.XmlNodeName.ARTIFACT_ID;
import static domain.constant.XmlNodeName.DEPENDENCY;
import static domain.constant.XmlNodeName.GROUP_ID;
import static domain.constant.XmlNodeName.PROJECT;
import static domain.constant.XmlNodeName.VERSION;

/**
 *
 */
public class PomFactory {
    private static final String VARIABLE = "^\\s*\\$\\s*\\{.+}\\s*$";

    /**
     *
     * @param currentPsiElement
     * @return
     */
    public Pom create(final PsiElement currentPsiElement) {
        final var currentXmlToken = castOrNull(currentPsiElement, XmlToken.class);
        Pom pom = null;
        if (currentPsiElement != null) {
            pom = create(currentXmlToken);
        }
        return pom;
    }

    private Pom create(final XmlToken currentXmlToken) {
        XmlText currentText = castOrNull(currentXmlToken.getParent(), XmlText.class);
        XmlTag currentTag = castOrNull(currentXmlToken.getParent(), XmlTag.class);
        if (currentText != null) {
            currentTag = castOrNull(currentText.getParent(), XmlTag.class);
        } else if (currentTag != null) {
            final var xmlTexts = extractXmlElement(currentTag, XmlText.class);
            if (xmlTexts.size() == 1) {
                currentText = xmlTexts.get(0);
            }
        }
        Pom pom = null;
        if (currentText != null && currentTag != null) {
            pom = createPom(currentText, currentTag);
        }
        return pom;
    }

    private Pom createPom(final XmlText version, final XmlTag versionTag) {
        final var noAttributesForVersionTag = versionTag.getAttributes().length == 0;
        final var isVersionTag = VERSION.getXmlName().equals(versionTag.getName());
        final var dependency = castOrNull(versionTag.getParent(), XmlTag.class);
        Pom pom = null;
        if (isVersionTag && noAttributesForVersionTag && dependency != null && !isVariable(version.getText())) {
            final var project = findBack(dependency, PROJECT.getXmlName());
            final var isDependencyTag = DEPENDENCY.getXmlName().equals(dependency.getName());
            final var noAttributesForDependencyTag = dependency.getAttributes().length == 0;
            if (project != null && isDependencyTag && noAttributesForDependencyTag) {
                pom = createPom(project, dependency, version);
            }
        }
        return pom;
    }

    private XmlTag findBack(final XmlTag xmlTag, final String name) {
        final var parent = castOrNull(xmlTag.getParent(), XmlTag.class);
        final XmlTag ret;
        if (parent != null) {
            if (name.equals(parent.getName())) {
                ret = parent;
            } else {
                ret = findBack(parent, name);
            }
        } else {
            ret = null;
        }
        return ret;
    }

    private Pom createPom(final XmlTag project, final XmlTag dependencyTag, final XmlText versionText) {
        return Pom.builder()
                .withNamespace(dependencyTag.getNamespace())
                .withProject(project)
                .withXmlDependency(XmlDependency.builder()
                        .withVersionXmlText(versionText)
                        .withDependency(createDependency(dependencyTag, versionText.getText()))
                        .build())
                .build();
    }

    private Dependency createDependency(final XmlTag dependencyTag, final String version) {
        final var xmlTagChildren = extractXmlElement(dependencyTag, XmlTag.class);
        final var groupIdTag = extractXmlTagByName(xmlTagChildren, GROUP_ID.getXmlName());
        final var artifactIdTag = extractXmlTagByName(xmlTagChildren, ARTIFACT_ID.getXmlName());
        Dependency dependency = null;
        if (groupIdTag != null && artifactIdTag != null) {
            dependency = createDependency(version, groupIdTag, artifactIdTag);
        }
        return dependency;
    }

    private <T extends XmlElement> List<T> extractXmlElement(final XmlTag dependencyTag, final Class<T> clazz) {
        return Arrays.stream(dependencyTag.getValue().getChildren())
                .filter(clazz::isInstance)
                .map(clazz::cast)
                .collect(Collectors.toList());
    }

    private Dependency createDependency(final String version, final XmlTag groupIdTag, final XmlTag artifactIdTag) {
        final var groupId = groupIdTag.getValue().getText();
        final var artifactId = artifactIdTag.getValue().getText();

        return Dependency.builder()
                .withArtifactId(artifactId)
                .withGroupId(groupId)
                .withVersion(version)
                .build();
    }

    private boolean isVariable(final String version) {
        return version.trim().matches(VARIABLE);
    }

    private XmlTag extractXmlTagByName(final List<XmlTag> xmlTagChildren, final String name) {
        return xmlTagChildren.stream()
                .filter(xmlTag -> name.equals(xmlTag.getName()))
                .findFirst()
                .orElse(null);
    }

    private <T, S extends T> S castOrNull(final T el, final Class<S> clazz) {
        S ret = null;
        if (el != null && clazz != null && clazz.isAssignableFrom(el.getClass())) {
            ret = clazz.cast(el);
        }
        return ret;
    }
}
