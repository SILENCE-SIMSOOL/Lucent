package silence.simsool.lucent.general.utils.notification;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.general.utils.useful.UDisplay;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UColor;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGPIPRenderer;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;

public class NotificationRenderer {

	private static long lastFrameTime = System.currentTimeMillis();

	public static void render(GuiGraphics graphics) {
		List<Notification> list = NotificationManager.getNotifications();
		if (list.isEmpty()) return;

		long now = System.currentTimeMillis();
		float deltaSeconds = Math.min(0.1f, (now - lastFrameTime) / 1000.0f);
		lastFrameTime = now;

		updateNotifications(list, deltaSeconds);
		if (list.isEmpty()) return;

		int screenWidth = UDisplay.getWidth();
		int screenHeight = UDisplay.getHeight();
		if (screenWidth <= 0 || screenHeight <= 0) return;

		NVGPIPRenderer.draw(graphics, 0, 0, screenWidth, screenHeight, () -> {
			float standardScale = NVGRenderer.getStandardGuiScale();
			if (standardScale <= 0.01f) standardScale = 1.0f;

			float canvasW = (float) UDisplay.getScreenWidth() / standardScale;
			float canvasH = (float) UDisplay.getScreenHeight() / standardScale;

			NVGRenderer.push();
			NVGRenderer.scale(standardScale, standardScale);

			float cardW = 320.0f;
			float marginRight = 18.0f;
			float marginBottom = 18.0f;
			float gap = 8.0f;
			float targetX = canvasW - cardW - marginRight;

			float currentTargetY = canvasH - marginBottom;

			// Render notifications from bottom to top
			for (int i = list.size() - 1; i >= 0; i--) {
				Notification n = list.get(i);
				float maxTextW = cardW - 46.0f - 16.0f;
				float[] titleBounds = NVGRenderer.wrappedTextBounds(n.getTitle(), maxTextW, Fonts.PRETENDARD_MEDIUM, 16.0f);
				float titleH = Math.max(16.0f, titleBounds[3] - titleBounds[1]);

				float msgH = 0.0f;
				if (n.hasMessage()) {
					float[] msgBounds = NVGRenderer.wrappedTextBounds(n.getMessage(), maxTextW, Fonts.PRETENDARD, 12.0f);
					msgH = Math.max(14.0f, msgBounds[3] - msgBounds[1]);
				}

				float cardH = n.hasMessage()
					? Math.max(78.0f, 18.0f + titleH + 8.0f + msgH + 18.0f)
					: Math.max(58.0f, 18.0f + titleH + 24.0f);
				currentTargetY -= cardH;

				if (n.getCurrentY() < 0f) {
					n.setCurrentY(currentTargetY + 20.0f);
				}
				n.setCurrentY(UAnimation.lerp(n.getCurrentY(), currentTargetY, 0.25f));

				float t = n.getAnimProgress();
				float ease = 1.0f - (float) Math.pow(1.0f - t, 3);
				float slideOffset = (1.0f - ease) * (cardW + marginRight + 20.0f);
				float drawX = targetX + slideOffset;
				float drawY = n.getCurrentY();

				int accentColor = 0xFF000000 | n.getType().getColor();
				int bgColor = switch (n.getType()) {
					case SUCCESS -> 0x880D2E25;
					case WARNING -> 0x8844310D;
					case ERROR -> 0x88440D16;
					default -> 0x880D1944;
				};

				int iconColor = switch (n.getType()) {
					case SUCCESS -> 0xFF34D399;
					case WARNING -> 0xFFFBBF24;
					case ERROR -> 0xFFF87171;
					default -> 0xFF60A5FA;
				};

				// Card background & outline
				NVGRenderer.dropShadow(drawX, drawY, cardW, cardH, 12, 4, 12);
				NVGRenderer.rect(drawX, drawY, cardW, cardH, bgColor, 12.0f);

				// Icon
				float iconSize = 20.0f;
				float iconX = drawX + 16.0f;
				float iconY = drawY + 18.0f;
				NVGRenderer.text(n.getIcon(), iconX, iconY, Fonts.MATERIAL_ICONS_ROUND, iconColor, iconSize);

				// Title text
				float contentX = drawX + 46.0f;
				float titleY = drawY + 18.0f;
				NVGRenderer.drawWrappedString(n.getTitle(), contentX, titleY, maxTextW, Fonts.PRETENDARD_MEDIUM, 16.0f, UColor.withAlpha(UIColors.PURE_WHITE, 225));

				// Message text
				if (n.hasMessage()) {
					float msgY = titleY + titleH + 8.0f;
					NVGRenderer.drawWrappedString(n.getMessage(), contentX, msgY, maxTextW, Fonts.PRETENDARD, 12.0f, 0xCCD1D5DB);
				}

				// Cool-down progress bar
				float barH = 2.5f;
				float barY = drawY + cardH - barH - 2.0f;
				float barTrackW = cardW - 24.0f;
				float barTrackX = drawX + 12.0f;

				int trackColor = (bgColor & 0x00FFFFFF) | 0x1A000000;
				NVGRenderer.rect(barTrackX, barY, barTrackW, barH, trackColor, 1.5f);

				float ratio = n.getProgressRatio();
				float barW = barTrackW * ratio;
				if (barW > 0.0f) {
					NVGRenderer.rect(barTrackX, barY, barW, barH, accentColor, 1.5f);
				}

				currentTargetY -= gap;
			}

			NVGRenderer.pop();
		});
	}

	private static void updateNotifications(List<Notification> list, float deltaSeconds) {
		float animSpeed = deltaSeconds * 4.0f; // ~250ms animation
		List<Notification> toRemove = new ArrayList<>();

		for (Notification n : list) {
			if (n.isExpired()) {
				n.setDismissing(true);
			}

			float progress = n.getAnimProgress();
			if (n.isDismissing()) {
				progress -= animSpeed;
				if (progress <= 0f) {
					toRemove.add(n);
					continue;
				}
			} else {
				if (progress < 1.0f) {
					progress = Math.min(1.0f, progress + animSpeed);
				}
			}
			n.setAnimProgress(progress);
		}

		if (!toRemove.isEmpty()) {
			list.removeAll(toRemove);
		}
	}

}