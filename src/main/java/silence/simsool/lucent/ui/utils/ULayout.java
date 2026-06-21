package silence.simsool.lucent.ui.utils;

public class ULayout {

	// ────────────────────────────────────────────────
	// 정렬 / 중앙 배치
	// ────────────────────────────────────────────────

	/** 컨테이너 안에서 자식 요소의 가로 중앙 X 좌표 */
	public static int centerX(int containerX, int containerW, int childW) {
		return containerX + (containerW - childW) / 2;
	}

	/** 컨테이너 안에서 자식 요소의 세로 중앙 Y 좌표 */
	public static int centerY(int containerY, int containerH, int childH) {
		return containerY + (containerH - childH) / 2;
	}

	/** 오른쪽 끝에 맞춘 X 좌표 */
	public static int alignRight(int containerX, int containerW, int childW) {
		return containerX + containerW - childW;
	}

	/** 아래쪽 끝에 맞춘 Y 좌표 */
	public static int alignBottom(int containerY, int containerH, int childH) {
		return containerY + containerH - childH;
	}

	// ────────────────────────────────────────────────
	// 범위 / 충돌
	// ────────────────────────────────────────────────

	/** 마우스가 영역 안에 있는지 */
	public static boolean isHovered(double mouseX, double mouseY, int x, int y, int w, int h) {
		return mouseX >= x && mouseX < x + w && mouseY >= y && mouseY < y + h;
	}

	/** 두 사각형이 겹치는지 */
	public static boolean overlaps(int ax, int ay, int aw, int ah, int bx, int by, int bw, int bh) {
		return ax < bx + bw && ax + aw > bx && ay < by + bh && ay + ah > by;
	}

	/** 점이 원 안에 있는지 */
	public static boolean isInsideCircle(double px, double py, double cx, double cy, double radius) {
		double dx = px - cx;
		double dy = py - cy;
		return dx * dx + dy * dy <= radius * radius;
	}

	// ────────────────────────────────────────────────
	// 크기 계산
	// ────────────────────────────────────────────────

	/** 비율 유지하면서 maxW, maxH 안에 맞추기 */
	public static int[] fitInside(int srcW, int srcH, int maxW, int maxH) {
		float scaleX = (float) maxW / srcW;
		float scaleY = (float) maxH / srcH;
		float scale = Math.min(scaleX, scaleY);
		return new int[]{(int)(srcW * scale), (int)(srcH * scale)};
	}

	/** 비율 유지하면서 minW, minH 꽉 채우기 (crop) */
	public static int[] fillCover(int srcW, int srcH, int targetW, int targetH) {
		float scaleX = (float) targetW / srcW;
		float scaleY = (float) targetH / srcH;
		float scale = Math.max(scaleX, scaleY);
		return new int[]{(int)(srcW * scale), (int)(srcH * scale)};
	}

	// ────────────────────────────────────────────────
	// 패딩 / 마진
	// ────────────────────────────────────────────────

	public record Insets(int top, int right, int bottom, int left) {
		public static Insets all(int v) {
			return new Insets(v, v, v, v);
		}

		public static Insets symmetric(int vertical, int horizontal) {
			return new Insets(vertical, horizontal, vertical, horizontal);
		}

		public int innerWidth(int outerW) {
			return outerW - left - right;
		}

		public int innerHeight(int outerH) {
			return outerH - top - bottom;
		}

		public int innerX(int x) {
			return x + left;
		}

		public int innerY(int y) {
			return y + top;
		}
	}

	// ────────────────────────────────────────────────
	// 진행값 변환
	// ────────────────────────────────────────────────

	/** 값 → 트랙 상의 픽셀 X 좌표 */
	public static int valueToTrackX(double value, double min, double max, int trackX, int trackW) {
		double t = (value - min) / (max - min);
		return trackX + (int)(t * trackW);
	}

	/** 트랙 X 좌표 → 값 */
	public static double trackXToValue(int px, int trackX, int trackW, double min, double max) {
		double t = UAnimation.clamp((double)(px - trackX) / trackW, 0.0, 1.0);
		return min + t * (max - min);
	}

	/** 값 → 트랙 상의 픽셀 Y 좌표 (세로 슬라이더) */
	public static int valueToTrackY(double value, double min, double max, int trackY, int trackH) {
		double t = (value - min) / (max - min);
		return trackY + (int)(t * trackH);
	}

	/** 트랙 Y 좌표 → 값 */
	public static double trackYToValue(int py, int trackY, int trackH, double min, double max) {
		double t = UAnimation.clamp((double)(py - trackY) / trackH, 0.0, 1.0);
		return min + t * (max - min);
	}

}