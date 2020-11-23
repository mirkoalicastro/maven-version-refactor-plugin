package controller;

import com.intellij.codeInsight.intention.HighPriorityAction;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import facade.VersionUpdatingFacade;
import org.jetbrains.annotations.NotNull;
import provider.PluginAvailabilityProvider;

/**
 *
 */
public class MavenVersionRefactorController extends PsiElementBaseIntentionAction implements IntentionAction, HighPriorityAction {
    private static final String PLUGIN_TEXT = "Refactor version as property";
    private static final String FAMILY_NAME = "Maven Version Refactor";

    private final VersionUpdatingFacade versionUpdatingFacade = new VersionUpdatingFacade();
    private final PluginAvailabilityProvider pluginAvailabilityProvider = new PluginAvailabilityProvider();

    /**
     *
     * @param project
     * @param editor
     * @param psiElement
     */
    @Override
    public void invoke(@NotNull final Project project, final Editor editor, @NotNull final PsiElement psiElement) {
        versionUpdatingFacade.replaceVersion(psiElement);
    }

    /**
     *
     * @param project
     * @param editor
     * @param psiElement
     * @return
     */
    @Override
    public boolean isAvailable(@NotNull final Project project, final Editor editor, @NotNull final PsiElement psiElement) {
        return pluginAvailabilityProvider.provide(psiElement);
    }

    /**
     *
     * @return
     */
    @NotNull
    @Override
    public @IntentionFamilyName String getFamilyName() {
        return FAMILY_NAME;
    }

    /**
     *
     * @return
     */
    @NotNull
    public String getText() {
        return PLUGIN_TEXT;
    }
}
