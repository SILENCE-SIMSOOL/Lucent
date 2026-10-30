package silence.simsool.lucent.guide;

import java.util.List;
import java.util.Locale;
import java.util.function.BooleanSupplier;

import silence.simsool.lucent.config.ModManager;
import silence.simsool.lucent.general.utils.useful.UDisplay;
import silence.simsool.lucent.general.utils.useful.UScreen;
import silence.simsool.lucent.ui.screens.ConfigScreen;
import silence.simsool.lucent.ui.screens.EditHUDScreen;
import silence.simsool.lucent.ui.utils.skija.SkijaRenderer;

/** Reusable ConfigScreen onboarding for a mod's own manager. */
public final class ConfigScreenGuide {

	private ConfigScreenGuide() {}

	public static Guide create(String id, ModManager manager, int priority) {
		return new Guide(id, "guide.config.name", priority, List.of(
			GuideStep.explain("guide.config.welcome.title", "guide.config.welcome.body"),
			GuideStep.await("guide.config.menu.title", "guide.config.menu.body",
				() -> UScreen.getScreen() instanceof EditHUDScreen || screen(manager) != null,
				() -> true, () -> null, () -> UScreen.setScreen(new EditHUDScreen(manager))),
			GuideStep.await("guide.config.mods.title", "guide.config.mods.body",
				() -> screen(manager) != null, () -> true,
				ConfigScreenGuide::modsButton, () -> UScreen.setScreen(new ConfigScreen(manager))),
			configStep(manager, "search", () -> screen(manager) != null && !screen(manager).getGuideQuery().isBlank()),
			configStep(manager, "settings", () -> screen(manager) != null && screen(manager).isGuideSettingsOpen()),
			configStep(manager, "Preferences", () -> page(manager, "Preferences")),
			configStep(manager, "Profiles", () -> page(manager, "Profiles")),
			configStep(manager, "Themes", () -> page(manager, "Themes")),
			GuideStep.explain("guide.config.done.title", "guide.config.done.body")
		));
	}

	private static GuideStep configStep(ModManager manager, String target, BooleanSupplier completed) {
		String key = "guide.config." + target.toLowerCase(Locale.ROOT);
		return GuideStep.await(key + ".title", key + ".body", completed,
			() -> screen(manager) != null,
			() -> screen(manager) == null ? null : screen(manager).getGuideTarget(target),
			() -> {
				ConfigScreen screen = screen(manager);
				if (screen == null) {
					screen = new ConfigScreen(manager);
					UScreen.setScreen(screen);
				}
				screen.activateGuideTarget(target);
			});
	}

	private static ConfigScreen screen(ModManager manager) {
		return UScreen.getScreen() instanceof ConfigScreen screen && screen.getGuideManager() == manager ? screen : null;
	}

	private static boolean page(ModManager manager, String page) {
		return screen(manager) != null && page.equals(screen(manager).getGuidePage());
	}

	private static GuideTarget modsButton() {
		if (!(UScreen.getScreen() instanceof EditHUDScreen)) return null;
		float scale = SkijaRenderer.getStandardGuiScale();
		float ratio = scale * UDisplay.getGuiScaledWidth() / UDisplay.getScreenWidth();
		return new GuideTarget((UDisplay.getScreenWidth() / scale - 164) / 2 * ratio,
			(UDisplay.getScreenHeight() / scale - 48) / 2 * ratio, 164 * ratio, 48 * ratio);
	}

}