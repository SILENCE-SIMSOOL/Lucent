package silence.simsool.lucent.client.dev.examplehuds;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.client.dev.examplemods.ChattingMod;
import silence.simsool.lucent.client.dev.examplemods.ExampleMod;
import silence.simsool.lucent.general.enums.HUDAlignment;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.models.abstracts.LucentHUD;
import silence.simsool.lucent.general.utils.UDisplay;
import silence.simsool.lucent.general.utils.UText;

public class ExampleHUD extends LucentHUD {

	public ExampleHUD() {
		super("example", 0.02f, 0.02f, 1.0f, HUDAlignment.LEFT);
	}

	@Override
	public RenderType getRenderType() {
		return RenderType.MINECRAFT;
	}

	@Override
	public float getPreviewWidth() {
		return 100;
	}

	@Override
	public float getPreviewHeight() {
		return 60;
	}

	@Override
	public boolean isEnabled() {
		return Lucent.config.getModule(ExampleMod.class).isEnabled && ChattingMod.AdvancedConfig;
	}

	@Override
	public void disable() {
		Lucent.config.getModule(ExampleMod.class).isEnabled = false;
	}

	@Override
	public void draw(GuiGraphics guiGraphics) {
		if (isEditHudOpen || UDisplay.isDebugScreen()) return;
		render(guiGraphics, false);
	}

	@Override
	public void preview(GuiGraphics guiGraphics) {
		render(guiGraphics, true);
	}

	private void render(GuiGraphics graphics, boolean preview) {
		float rx = getRenderX();
		float ry = getRenderY();

		UText.drawText(graphics, String.format("[X]: " + mc.player.getBlockX()), rx, ry, scale);
		UText.drawText(graphics, "abcdefghijklnmopqrstuvwxyz ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", rx, ry + 6, scale);
	}

}