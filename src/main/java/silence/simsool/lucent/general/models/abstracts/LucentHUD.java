package silence.simsool.lucent.general.models.abstracts;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.config.api.LucentAPI;
import silence.simsool.lucent.general.enums.HUDAlignment;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.utils.useful.UDisplay;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public abstract class LucentHUD {
	public static boolean isEditHudOpen = false;
	public final String id;
	public final Class<? extends Mod> moduleClass;
	public float x;
	public float y;
	public float scale; // (0.5 ~ 2.0 / 0.1 unit)
	public HUDAlignment alignment;

	protected LucentHUD(String id, float defaultX, float defaultY, float defaultScale, HUDAlignment defaultAlignment) {
		this(id, null, defaultX, defaultY, defaultScale, defaultAlignment);
	}

	protected LucentHUD(String id, Class<? extends Mod> moduleClass, float defaultX, float defaultY, float defaultScale, HUDAlignment defaultAlignment) {
		this.id = id;
		this.moduleClass = moduleClass;
		this.x = defaultX;
		this.y = defaultY;
		this.scale = clampScale(defaultScale);
		this.alignment = defaultAlignment;
	}

	/**
	 * Determines the rendering type for this HUD.
	 * <ul>
	 * <li>MINECRAFT: Uses standard Minecraft GUI rendering.</li>
	 * <li>NANOVG: Uses high-fidelity rendering via NVGPIPRenderer.</li>
	 * </ul>
	 */
	public abstract RenderType getRenderType();
	
	/**
	 * Determines if this HUD element should be rendered.
	 * Subclasses can override this to tie visibility to mod settings.
	 */
	public boolean isEnabled() {
		return moduleClass == null || Lucent.config.isModuleEnabled(moduleClass);
	}

	/**
	 * Disables this HUD element.
	 * Subclasses should override this to disable the associated config module.
	 */
	public void disable() {
		if (moduleClass != null) {
			Lucent.config.setModuleEnabled(moduleClass, false);
		}
	}

	/**
	 * Renders the actual HUD in-game.
	 * <ul>
	 * <li>Rendering should be skipped if {@link #isEditHudOpen} is true.</li>
	 * <li>To stop rendering when the F3 debug screen is active, check UDisplay.isDebugScreen().</li>
	 * <li>Use real-time data for rendering.</li>
	 * </ul>
	 */
	public abstract void draw(GuiGraphics guiGraphics);

	/**
	 * Renders a preview for the EditHUD screen.
	 * <ul>
	 * <li>Used exclusively for visual adjustment within the editor.</li>
	 * <li>Does not need to rely on actual game states or live data.</li>
	 * </ul>
	 */
	public abstract void preview(GuiGraphics guiGraphics);

	/** Returns the base width used for preview and scaling calculations. */
	public abstract float getPreviewWidth();

	/** Returns the base height used for preview and scaling calculations. */
	public abstract float getPreviewHeight();

	/** Returns the width adjusted by the current scale. */
	public float getScaledWidth() {
		return getPreviewWidth() * scale;
	}

	/** Returns the height adjusted by the current scale. */
	public float getScaledHeight() {
		return getPreviewHeight() * scale;
	}

	/**
	 * Clamps the scale value between 0.5 and 2.0, rounded to the nearest tenth.
	 * @param scale The input scale to be clamped.
	 * @return The clamped and rounded scale value.
	 */
	public static float clampScale(float scale) {
		float rounded = Math.round(scale * 10f) / 10f;
		return Math.max(0.5f, Math.min(2.0f, rounded));
	}

	/**
	 * Calculates the actual X coordinate for rendering based on the alignment and relative ratio.
	 * @return The calculated X coordinate.
	 */
	public float getRenderX() {
		float sw = UDisplay.getWidth();
		float gs = NVGRenderer.getStandardGuiScale();
		float virtualW = sw / gs;

		return switch (alignment) {
			case LEFT -> x * virtualW;
			case CENTER -> x * virtualW - getScaledWidth() / 2f;
			case RIGHT -> x * virtualW - getScaledWidth();
		};
	}

	/**
	 * Returns the Y coordinate for rendering based on the relative ratio.
	 * @return The Y coordinate.
	 */
	public float getRenderY() {
		float sh = UDisplay.getHeight();
		float gs = NVGRenderer.getStandardGuiScale();
		float virtualH = sh / gs;

		return y * virtualH;
	}

	/** Saves the current HUD state to the configuration file. */
	public void save() {
		LucentAPI.getHUDManager().save();
	}
}