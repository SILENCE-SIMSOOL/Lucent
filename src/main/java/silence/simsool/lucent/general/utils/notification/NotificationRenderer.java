package silence.simsool.lucent.general.utils.notification;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import silence.simsool.lucent.general.utils.useful.UDisplay;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UColor;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.skija.Fonts;
import silence.simsool.lucent.ui.utils.skija.SkijaRenderer;

public class NotificationRenderer {

	private static long lastFrameTime = System.currentTimeMillis();

	public static void render(GuiGraphicsExtractor graphics) {
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

		SkijaRenderer.draw(graphics, 0, 0, screenWidth, screenHeight, () -> {
			float standardScale = SkijaRenderer.getStandardGuiScale();
			if (standardScale <= 0.01f) standardScale = 1.0f;

			float canvasW = (float) UDisplay.getScreenWidth() / standardScale;
			float canvasH = (float) UDisplay.getScreenHeight() / standardScale;

			SkijaRenderer.push();
			SkijaRenderer.scale(standardScale, standardScale);

			float cardW = 320.0f;
			float marginRight = 18.0f;
			float marginBottom = 18.0f;
			float gap = 8.0f;
			float targetX = canvasW - cardW - marginRight;

			float currentTargetY = canvasH - marginBottom;

			// Render notifications from bottom to top
			for (int i = list.size() - 1; i >= 0; i--) {
				Notification n = list.get(i);
				float cardH = n.hasMessage() ? 58 + 20 : 58;
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
				SkijaRenderer.dropShadow(drawX, drawY, cardW, cardH, 12, 4, 12);
				SkijaRenderer.rect(drawX, drawY, cardW, cardH, bgColor, 12.0f);

				// Icon
				float iconSize = 20.0f;
				float iconX = drawX + 16.0f;
				float iconY = drawY + 18.0f;
				SkijaRenderer.text(n.getIcon(), iconX, iconY, Fonts.MATERIAL_ICONS_ROUND, iconColor, iconSize);

				// Title text
				float contentX = drawX + 46.0f;
				float titleY = drawY + 18.0f;
				SkijaRenderer.text(n.getTitle(), contentX, titleY, Fonts.PRETENDARD_MEDIUM, UColor.withAlpha(UIColors.PURE_WHITE, 225), 16.0f);

				// Message text
				if (n.hasMessage()) {
					SkijaRenderer.text(n.getMessage(), contentX, drawY + 44.0f, Fonts.PRETENDARD, 0xCCD1D5DB, 12.0f);
				}

				// Cool-down progress bar
				float barH = 2.5f;
				float barY = drawY + cardH - barH - 2.0f;
				float barTrackW = cardW - 24.0f;
				float barTrackX = drawX + 12.0f;

				int trackColor = (bgColor & 0x00FFFFFF) | 0x1A000000;
				SkijaRenderer.rect(barTrackX, barY, barTrackW, barH, trackColor, 1.5f);

				float ratio = n.getProgressRatio();
				float barW = barTrackW * ratio;
				if (barW > 0.0f) {
					SkijaRenderer.rect(barTrackX, barY, barW, barH, accentColor, 1.5f);
				}

				currentTargetY -= gap;
			}

			SkijaRenderer.pop();
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