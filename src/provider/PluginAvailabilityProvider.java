package provider;

import com.intellij.psi.PsiElement;
import factory.PomFactory;

/**
 *
 */
public class PluginAvailabilityProvider {
    private final PomFactory pomFactory = new PomFactory();

    /**
     *
     * @param psiElement
     * @return
     */
    public boolean provide(final PsiElement psiElement) {
        return pomFactory.create(psiElement) != null;
    }
}
