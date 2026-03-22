package silence.simsool.lucent.general.utils;

import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class URender {

	/**
	 * 토글 버튼 그리기
	 * @param onProgress 0.0(off) ~ 1.0(on) 상태 진행도
	 * @param hoverProgress 0.0(기본) ~ 1.0(호버됨) 상태 진행도
	 */
	public static void drawToggleButton(float x, float y, float w, float h, float onProgress, float hoverProgress) {
		int bgOff = UIColors.DIM_GRAY;
		int bgOn = UIColors.ACCENT_BLUE;
		int bgColor = UAnimation.lerpColor(bgOff, bgOn, onProgress);

		float radius = h / 2f;
		NVGRenderer.rect(x, y, w, h, bgColor, radius);

		float padding = 2f;
		float baseCircleRadius = (h - padding * 2) / 2f;
		// 호버 시 원이 약간 커지는 애니메이션 (최대 1.5px 확장)
		float hoverExpansion = 1.5f * hoverProgress;
		float currentCircleRadius = baseCircleRadius + hoverExpansion;

		float minX = x + padding + baseCircleRadius;
		float maxX = x + w - padding - baseCircleRadius;
		float circleX = UAnimation.lerp(minX, maxX, onProgress);
		float circleY = y + h / 2f;

		// 원에 그림자를 줘서 입체감 추가
		NVGRenderer.dropShadow(circleX - currentCircleRadius, circleY - currentCircleRadius, currentCircleRadius * 2, currentCircleRadius * 2, 3f, 0f, currentCircleRadius);
		NVGRenderer.circle(circleX, circleY, currentCircleRadius, UIColors.PURE_WHITE);
	}

}