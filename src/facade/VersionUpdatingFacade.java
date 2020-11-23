package facade;

import com.intellij.psi.PsiElement;
import domain.Pom;
import factory.PomFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import provider.FreeVersionProvider;
import updater.PropertiesUpdater;

/**
 *
 */
public class VersionUpdatingFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionUpdatingFacade.class);

    private final FreeVersionProvider freeVersionProvider = new FreeVersionProvider();
    private final PropertiesUpdater propertiesUpdater = new PropertiesUpdater();
    private final PomFactory pomFactory = new PomFactory();

    /**
     *
     * @param psiElement
     */
    public void replaceVersion(final PsiElement psiElement) {
        final var pom = pomFactory.create(psiElement);
        if (pom != null) {
            doExecute(pom);
        } else {
            LOGGER.error("Unexpected error on {}", psiElement);
        }
    }

    private void doExecute(final Pom pom) {
        final var freeVersion = getFreeVersion(pom);
        if (freeVersion != null) {
            replaceVersionWithVariable(pom, freeVersion);
            addVersionInProperties(pom, freeVersion);
        } else {
            LOGGER.error("Unexpected error: cannot retrieve a free version starting from pom {}", pom);
        }
    }

    private void addVersionInProperties(final Pom pom, final String freeVersion) {
        propertiesUpdater.addVersionToProperties(pom, freeVersion);
    }

    private void replaceVersionWithVariable(final Pom pom, final String freeVersion) {
        pom.getXmlDependency().getVersionXmlText().setValue(String.format("${%s}", freeVersion));
    }

    private String getFreeVersion(final Pom pom) {
        return freeVersionProvider.getFreeVersion(pom.getProject(), pom.getXmlDependency().getDependency());
    }
}
