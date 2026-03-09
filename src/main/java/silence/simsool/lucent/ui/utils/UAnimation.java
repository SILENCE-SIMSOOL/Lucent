package silence.simsool.lucent.ui.utils;

/**
 * Animation related utilities.
 * Includes easing functions, interpolation, and time-based effects.
 */
public class UAnimation {

	// ────────────────────────────────────────────────
	// Easing Functions (Input t: 0.0 ~ 1.0, Output: 0.0 ~ 1.0)
	// ────────────────────────────────────────────────
	public static class Easing {

		public static float linear(float t) {
			return t;
		}

		public static float easeIn(float t) {
			return t * t;
		}

		public static float easeOut(float t) {
			return 1f - (1f - t) * (1f - t);
		}

		public static float easeInOut(float t) {
			return t < 0.5f ? 2f * t * t : 1f - (float) Math.pow(-2f * t + 2f, 2) / 2f;
		}

		public static float easeInCubic(float t) {
			return t * t * t;
		}

		public static float easeOutCubic(float t) {
			return 1f - (1f - t) * (1f - t) * (1f - t);
		}

		public static float easeInOutCubic(float t) {
			return t < 0.5f ? 4f * t * t * t : 1f - (float) Math.pow(-2f * t + 2f, 3) / 2f;
		}

		/** Back ease out (Slight overshoot) */
		public static float spring(float t) {
			float c1 = 1.70158f;
			float c3 = c1 + 1f;
			return 1f + c3 * (float) Math.pow(t - 1f, 3) + c1 * (float) Math.pow(t - 1f, 2);
		}

		/** Elastic ease out */
		public static float elastic(float t) {
			if (t == 0 || t == 1) return t;
			float c4 = (float) (2 * Math.PI) / 3f;
			return (float) (Math.pow(2, -10 * t) * Math.sin((t * 10 - 0.75) * c4) + 1);
		}

		/** Bounce ease out */
		public static float bounce(float t) {
			float n1 = 7.5625f;
			float d1 = 2.75f;
			if (t < 1f / d1) {
				return n1 * t * t;
			} else if (t < 2f / d1) {
				t -= 1.5f / d1;
				return n1 * t * t + 0.75f;
			} else if (t < 2.5f / d1) {
				t -= 2.25f / d1;
				return n1 * t * t + 0.9375f;
			} else {
				t -= 2.625f / d1;
				return n1 * t * t + 0.984375f;
			}
		}
	}

	// ────────────────────────────────────────────────
	// Interpolation (Lerp)
	// ────────────────────────────────────────────────

	public static float lerp(float a, float b, float t) {
		return a + (b - a) * t;
	}

	public static double lerp(double a, double b, double t) {
		return a + (b - a) * t;
	}

	public static int lerp(int a, int b, float t) {
		return (int)(a + (b - a) * t);
	}

	public static int lerpColor(int c1, int c2, float t) {
		return UColor.lerpColor(c1, c2, t);
	}

	/**
	 * Smooth tracking interpolation (delta-based).
	 * 'current' follows 'target' at the specified 'speed'.
	 * Snaps to target if the difference is below 'threshold'.
	 */
	public static float lerpSnap(float current, float target, float speed, float delta, float threshold) {
		float diff = target - current;
		if (Math.abs(diff) < threshold) return target;
		return current + diff * Math.min(speed * delta, 1f);
	}

	public static float lerpSnap(float current, float target, float speed, float delta) {
		return lerpSnap(current, target, speed, delta, 0.001f);
	}

	// ────────────────────────────────────────────────
	// Clamping
	// ────────────────────────────────────────────────

	public static float clamp(float val, float min, float max) {
		return Math.max(min, Math.min(max, val));
	}

	public static double clamp(double val, double min, double max) {
		return Math.max(min, Math.min(max, val));
	}

	public static int clamp(int val, int min, int max) {
		return Math.max(min, Math.min(max, val));
	}

	// ────────────────────────────────────────────────
	// Time-based Effects
	// ────────────────────────────────────────────────

	/** Pulsing alpha value (0~1 sine wave) */
	public static float getPulseAlpha(float speed) {
		return (float)(Math.sin(System.currentTimeMillis() / 1000.0 * speed * Math.PI) * 0.5 + 0.5);
	}

	/** Wave offset */
	public static float getWaveOffset(float speed, float amplitude) {
		return (float)(Math.sin(System.currentTimeMillis() / 1000.0 * speed * Math.PI) * amplitude);
	}

	/** Current time-based 0~1 cycle (period: seconds) */
	public static float getCycle(float period) {
		return (float)((System.currentTimeMillis() / 1000.0 % period) / period);
	}

	// ────────────────────────────────────────────────
	// Value Snapping
	// ────────────────────────────────────────────────

	/** Snap value to step units (for sliders) */
	public static double snapToStep(double value, double step) {
		if (step <= 0) return value;
		return Math.round(value / step) * step;
	}

	/** Format based on decimal places (matching step) */
	public static String formatForStep(double value, double step) {
		if (step >= 1) return String.valueOf((int) Math.round(value));
		int decimals = 0;
		double s = step;

		while (s < 1 && decimals < 10) {
			s *= 10;
			decimals++;
		}
		return String.format("%." + decimals + "f", value);
	}

	// ────────────────────────────────────────────────
	// Animation State Helper
	// ────────────────────────────────────────────────

	/**
	 * Updates progress between 0 and 1 based on delta.
	 * @param current  Current progress (0~1)
	 * @param forward  true=forward, false=backward
	 * @param speed    Change per second (1 = complete in 1 second)
	 * @param delta    Tick delta (seconds)
	 */
	public static float stepProgress(float current, boolean forward, float speed, float delta) {
		float change = speed * delta;
		if (forward) return Math.min(1f, current + change);
		else return Math.max(0f, current - change);
	}
}