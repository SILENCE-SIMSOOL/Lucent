package silence.simsool.lucent.ui.widget.components;

import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UColor;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.UIWidget;

public class ToggleButton extends UIWidget {
	private static final float PADDING = 2.0f;
	private static final float ANIM_SPEED = 0.4f;

	private boolean value;
	private float animProgress; // 0.0 (OFF) -> 1.0 (ON)
	private float hoverAnim;    // 0.0 ~ 1.0 (hover expansion)
	private Consumer<Boolean> onChange;
	private String id;

	public ToggleButton(int x, int y, int width, int height, boolean initialValue) {
		super(x, y, width, height);
		this.value = initialValue;
		this.animProgress = initialValue ? 1.0f : 0.0f;
	}

	@Override
	protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		NVGRenderer.push();
		
		float target = value ? 1.0f : 0.0f;
		animProgress = animProgress + (target - animProgress) * ANIM_SPEED * delta * 2.5f;
		if (Math.abs(target - animProgress) < 0.005f) animProgress = target;

		hoverAnim = UAnimation.stepProgress(hoverAnim, hovered, 8.0f, delta);

		int offColor = UIColors.MUTED;
		int onColor  = UIColors.ACCENT_BLUE;

		// Track Color: Lerp based on ON/OFF state
		int baseColor = UAnimation.lerpColor(offColor, onColor, animProgress);
		if (hovered && !value) baseColor = UColor.brighten(offColor, 0.2f);
		else if (hovered && value) baseColor = UColor.brighten(onColor, 0.1f);

		int trackColor = baseColor;
		if (!enabled) trackColor = UIColors.withAlpha(UIColors.MUTED, 100);

		float thumbSize = height - PADDING * 2;
		float baseRadius = thumbSize / 2.0f;
		float expandedRadius = baseRadius;

		float minX = x + PADDING + baseRadius;
		float maxX = x + width - PADDING - baseRadius;
		float thumbX = UAnimation.lerp(minX, maxX, animProgress);
		float thumbY = y + height / 2.0f;

		NVGRenderer.rect(x, y, width, height, trackColor, height / 2.0f);
		NVGRenderer.dropShadow(thumbX - expandedRadius, thumbY - expandedRadius, expandedRadius * 2, expandedRadius * 2, 4f, 0f, expandedRadius);
		NVGRenderer.circle(thumbX, thumbY, expandedRadius, UIColors.PURE_WHITE);
		
		NVGRenderer.pop();
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!enabled || !visible) return false;
		if (button == 0 && isMouseOver(mouseX, mouseY)) {
			toggle();
			return true;
		}
		return false;
	}

	public void toggle() {
		setValue(!value, true);
	}

	public boolean getValue() {
		return value;
	}

	public void setValue(boolean value) {
		setValue(value, false);
	}

	public void setValue(boolean value, boolean animate) {
		this.value = value;
		if (!animate) this.animProgress = value ? 1.0f : 0.0f;
		if (onChange != null) onChange.accept(value);
	}

	public void setOnChange(Consumer<Boolean> onChange) {
		this.onChange = onChange;
	}

	public void setAnimProgress(float progress) {
		this.animProgress = progress;
	}

	public float getAnimProgress() {
		return animProgress;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

// Later..
//	public ToggleButton setTrackColors(int offColor, int onColor) {
//		this.trackOffColor = offColor;
//		this.trackOnColor = onColor;
//		return this;
//	}
//
//	public ToggleButton setThumbColor(int color) {
//		this.thumbColor = color;
//		return this;
//	}
}