package silence.simsool.lucent.ui.utils;
/**
 * 사각형의 4개 모서리 라운딩 반지름을 담는 레코드
 * 순서: topLeft, topRight, bottomLeft, bottomRight
 */
public record UCorner(float tl, float tr, float bl, float br) {

	/** 4면 모두 동일한 라운딩 */
	public static UCorner of(float r) {
		return new UCorner(r, r, r, r);
	}

	/** 라운딩 없음 */
	public static UCorner none() {
		return new UCorner(0, 0, 0, 0);
	}

	/** 위쪽(tl, tr)만 라운딩 */
	public static UCorner top(float r) {
		return new UCorner(r, r, 0, 0);
	}

	/** 아래쪽(bl, br)만 라운딩 */
	public static UCorner bottom(float r) {
		return new UCorner(0, 0, r, r);
	}

	/** 왼쪽(tl, bl)만 라운딩 */
	public static UCorner left(float r) {
		return new UCorner(r, 0, r, 0);
	}

	/** 오른쪽(tr, br)만 라운딩 */
	public static UCorner right(float r) {
		return new UCorner(0, r, 0, r);
	}

	/** 대각선 — 왼쪽위 + 오른쪽아래 */
	public static UCorner diagonal(float r) {
		return new UCorner(r, 0, 0, r);
	}

	/** 대각선 반대 — 오른쪽위 + 왼쪽아래 */
	public static UCorner diagonalAlt(float r) {
		return new UCorner(0, r, r, 0);
	}

	/** 모든 코너가 0인지 */
	public boolean isNone() {
		return tl == 0 && tr == 0 && bl == 0 && br == 0;
	}

	/** 최대 반지름값 (박스 크기 제한 계산용) */
	public float max() {
		return Math.max(Math.max(tl, tr), Math.max(bl, br));
	}

	/**
	 * 박스 크기에 맞게 반지름을 클램프
	 * 너무 크면 박스가 이상해지므로 w/2, h/2 초과 방지
	 */
	public UCorner clamp(float maxR) {
		return new UCorner(
			Math.min(tl, maxR),
			Math.min(tr, maxR),
			Math.min(bl, maxR),
			Math.min(br, maxR)
		);
	}

	public UCorner clampToBox(float w, float h) {
		float maxR = Math.min(w, h) / 2f;
		return clamp(maxR);
	}
}