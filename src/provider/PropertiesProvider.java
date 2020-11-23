package provider;

import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlTag;
import domain.Dependency;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static domain.constant.XmlNodeName.PROPERTIES;

/**
 *
 */
public class PropertiesProvider {

    /**
     *
     * @param project
     * @return
     */
    public XmlTag provide(final XmlTag project) {
        return Optional.ofNullable(project)
                .map(PsiElement::getChildren)
                .map(this::extractProperties)
                .orElse(null);
    }

    private XmlTag extractProperties(PsiElement[] psiElements) {
        return Arrays.stream(psiElements)
                .filter(XmlTag.class::isInstance)
                .map(XmlTag.class::cast)
                .filter(xmlTag -> PROPERTIES.getXmlName().equals(xmlTag.getName().toLowerCase()))
                .findFirst()
                .orElse(null);
    }
}
