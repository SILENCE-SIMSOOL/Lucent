package silence.simsool.lucent.client.dev.examplemods;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.enums.HUDAlignment;
import silence.simsool.lucent.general.enums.RenderType;
import silence.simsool.lucent.general.models.abstracts.LucentHUD;
import silence.simsool.lucent.general.utils.UDisplay;
import silence.simsool.lucent.general.utils.UText;

public class TestHUD extends LucentHUD {

	public TestHUD() {
		super("testhud", 0.02f, 0.02f, 1.0f, HUDAlignment.LEFT);
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
		return Lucent.config.isModuleEnabled(ExampleMod.class);
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
		if (!preview && mc.player == null) return;

		int sw = UDisplay.getGuiScaledWidth();
		int sh = mc.getWindow().getGuiScaledHeight();

		float rx = x * sw;
		float ry = y * sh;

		UText.drawText(graphics, String.format("XT: " + mc.player.getBlockX()), rx, ry, scale);
	}

}