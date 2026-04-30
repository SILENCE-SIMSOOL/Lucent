package silence.simsool.lucent.ui.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
			}

			else if (t < 2f / d1) {
				t -= 1.5f / d1;
				return n1 * t * t + 0.75f;
			}

			else if (t < 2.5f / d1) {
				t -= 2.25f / d1;
				return n1 * t * t + 0.9375f;
			}

			else {
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
		double snapped = Math.round(value / step) * step;
		
		int decimals = 0;
		double s = step;
		while (s < 1 && decimals < 10) {
			s *= 10;
			decimals++;
		}
		
		BigDecimal bd = new BigDecimal(Double.toString(snapped));
		bd = bd.setScale(decimals, RoundingMode.HALF_UP);
		return bd.doubleValue();
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
		
		String formatted = String.format("%." + decimals + "f", value);
		if (formatted.contains(".")) {
			while (formatted.endsWith("0") && !formatted.endsWith(".0")) {
				formatted = formatted.substring(0, formatted.length() - 1);
			}
		}
		return formatted;
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


	public static float map(float value, float min1, float max1, float min2, float max2) {
		return min2 + (value - min1) * (max2 - min2) / (max1 - min1);
	}

	public static float round(float value, int places) {
		float scale = (float) Math.pow(10, places);
		return (float) Math.round(value * scale) / scale;
	}

	/** 두 값이 오차 범위(epsilon) 내에서 같은지 확인 (부동소수점 비교용) */
	public static boolean epsilonEquals(float a, float b, float epsilon) {
		return Math.abs(a - b) < epsilon;
	}

	/** 0.0 ~ 1.0 사이의 값을 반환*/
	public static float saturate(float v) {
		return clamp(v, 0f, 1f);
	}

	/** 값이 특정 범위 안에 있는지 확인 */
	public static boolean inRange(float v, float min, float max) {
		return v >= min && v <= max;
	}

	/** 최대값과 최소값 사이에서 순환 (각도 계산 등에 유용) */
	public static float wrap(float v, float min, float max) {
		float range = max - min;
		return (float) (min + ((((v - min) % range) + range) % range));
	}
}