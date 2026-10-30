package silence.simsool.lucent.guide;

/** Bounds in Minecraft GUI coordinates, before any Lucent rendering scale. */
public record GuideTarget(float x, float y, float width, float height) {

	public boolean contains(double mx, double my) {
		return mx >= x && mx <= x + width && my >= y && my <= y + height;
	}

}