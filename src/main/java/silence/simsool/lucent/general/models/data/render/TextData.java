package silence.simsool.lucent.general.models.data.render;

import org.joml.Quaternionf;

import net.minecraft.client.gui.Font;
import net.minecraft.world.phys.Vec3;

public class TextData {
	public String text;
	public Vec3 pos;
	public float scale;
	public boolean depth;
	public Quaternionf cameraRotation;
	public Font font;
	public float textWidth;
	public int color;
	public boolean shadow;

	public TextData(String text, Vec3 pos, float scale, boolean depth, Quaternionf rotation, Font font, float width, int color, boolean shadow) {
		set(text, pos, scale, depth, rotation, font, width, color, shadow);
	}

	public TextData(String text, Vec3 pos, float scale, boolean depth, Quaternionf rotation, Font font, float width) {
		set(text, pos, scale, depth, rotation, font, width, -1, true);
	}

	public void set(String text, Vec3 pos, float scale, boolean depth, Quaternionf rotation, Font font, float width, int color, boolean shadow) {
		this.text = text;
		this.pos = pos;
		this.scale = scale;
		this.depth = depth;
		this.cameraRotation = rotation;
		this.font = font;
		this.textWidth = width;
		this.color = color;
		this.shadow = shadow;
	}
}