package silence.simsool.lucent.general.models.data.render;

import org.joml.Quaternionf;

import net.minecraft.client.gui.Font;
import net.minecraft.world.phys.Vec3;

public class TextData {
	public final String text;
	public final Vec3 pos;
	public final float scale;
	public final boolean depth;
	public final Quaternionf cameraRotation;
	public final Font font;
	public final float textWidth;
	public final int color;
	public final int backgroundColor;
	public final int outlineColor;
	public final boolean shadow;

	public TextData(String text, Vec3 pos, float scale, boolean depth, Quaternionf rotation, Font font, float width, int color, int backgroundColor, int outlineColor, boolean shadow) {
		this.text = text;
		this.pos = pos;
		this.scale = scale;
		this.depth = depth;
		this.cameraRotation = rotation;
		this.font = font;
		this.textWidth = width;
		this.color = color;
		this.backgroundColor = backgroundColor;
		this.outlineColor = outlineColor;
		this.shadow = shadow;
	}

	public TextData(String text, Vec3 pos, float scale, boolean depth, Quaternionf rotation, Font font, float width, int color, int backgroundColor, int outlineColor) {
		this(text, pos, scale, depth, rotation, font, width, color, backgroundColor, outlineColor, true);
	}
}