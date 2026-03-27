package silence.simsool.lucent.ui.widget;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.gui.GuiGraphics;
import silence.simsool.lucent.ui.font.LucentFont;
import silence.simsool.lucent.ui.utils.UAnimation;
import silence.simsool.lucent.ui.utils.UColor;
import silence.simsool.lucent.ui.utils.UIColors;
import silence.simsool.lucent.ui.utils.ULayout;
import silence.simsool.lucent.ui.utils.nvg.Fonts;
import silence.simsool.lucent.ui.utils.nvg.NVGRenderer;
import silence.simsool.lucent.ui.widget.base.UIWidget;

public class Selector extends UIWidget {
	private int borderColor = UIColors.ITEM_BORDER;
	private int textColor = UIColors.TEXT_PRIMARY;
	private int dropdownBgColor = UIColors.WIN_BG;
	private int itemHoverColor = UIColors.CARD_HOVER;
	private int separatorColor = UIColors.DIVIDER;

	private static int PADDING = 14;
	private static int ARROW_W = 16;
	private static int ITEM_HEIGHT = 38;
	private static int MAX_VISIBLE = 6;
	private static float FONT_SIZE = 14f;

	private List<String> options = new ArrayList<>();
	private int selectedIndex = 0;
	private boolean isOpen = false;
	private float dropdownAnim = 0f; 
	private int hoveredItem = -1;
	private int scrollOffset = 0;

	private Consumer<String> onChange;
	private Consumer<Integer> onChangeIndex;

	public Selector(int x, int y, int width, int height) {
		super(x, y, width, height);
	}

	public Selector(int x, int y, int width, int height, List<String> options) {
		super(x, y, width, height);
		this.options = new ArrayList<>(options);
	}

	@Override
	protected void renderWidget(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		NVGRenderer.push();
		dropdownAnim = UAnimation.stepProgress(dropdownAnim, isOpen, 12f, delta);

		int bg = UColor.withAlpha(UIColors.PURE_BLACK, 60);
		int border = isOpen ? UIColors.ACCENT_BLUE : (hovered ? UColor.withAlpha(UIColors.ACCENT_BLUE, 180) : UIColors.ITEM_BORDER);

		NVGRenderer.rect(x, y, width, height, bg, 8f);
		NVGRenderer.outlineRect(x, y, width, height, 1, border, 8f);

		String currentText = options.isEmpty() ? "" : options.get(selectedIndex);
		int textAreaW = width - PADDING * 2 - ARROW_W - 4;
		String clipped = fitText(currentText, textAreaW, Fonts.PRETENDARD_MEDIUM);
		
		int ty = y + (height - 14) / 2;
		NVGRenderer.text(clipped, x + PADDING, ty, Fonts.PRETENDARD_MEDIUM, UIColors.TEXT_PRIMARY, 14f);

		drawChevron(x + width - PADDING - 10, y + height / 2, dropdownAnim);
		NVGRenderer.pop();
	}

	private void drawChevron(float cx, float cy, float openProgress) {
		float size = 5f;
		float angle = UAnimation.lerp(0, (float)Math.PI, openProgress);
		
		NVGRenderer.push();
		NVGRenderer.translate(cx, cy);
		NVGRenderer.rotate(angle);
		
		NVGRenderer.outlineTriangle(-size, -size/2, size, -size/2, 0, size/2, 1.5f, UIColors.TEXT_SECONDARY);
		
		NVGRenderer.pop();
	}

	@Override
	public void renderOverlay(GuiGraphics ctx, int mouseX, int mouseY, float delta) {
		if (dropdownAnim <= 0.01f) return;
		NVGRenderer.push();
		int visibleCount = Math.min(options.size(), MAX_VISIBLE);
		int totalH = visibleCount * ITEM_HEIGHT;
		int visibleH = (int)(totalH * UAnimation.Easing.easeOut(dropdownAnim));

		int dx = x;
		int dy = y + height + 2;

		NVGRenderer.pushScissor(dx, dy, width, visibleH);
		NVGRenderer.rect(dx, dy, width, totalH, dropdownBgColor, 10f);
		NVGRenderer.outlineRect(dx, dy, width, totalH, 1, borderColor, 10f);

		for (int i = 0; i < visibleCount; i++) {
			int idx = i + scrollOffset;
			if (idx >= options.size()) break;
			int iy = dy + i * ITEM_HEIGHT;

			boolean isSelected = (idx == selectedIndex);
			boolean isItemHovered = (hoveredItem == idx);

			if (i < visibleCount - 1) {
				NVGRenderer.rect(dx + 4, iy + ITEM_HEIGHT - 1, width - 8, 1, separatorColor);
			}

			if (isSelected || isItemHovered) {
				int targetBg = isSelected ? UColor.withAlpha(UIColors.ACCENT_BLUE, 100) : itemHoverColor;
				if (i == 0) NVGRenderer.rect(dx, iy, width, ITEM_HEIGHT, targetBg, 10, 10, 0, 0);
				else if (i == visibleCount - 1) NVGRenderer.rect(dx, iy, width, ITEM_HEIGHT, targetBg, 0, 0, 10, 10);
				else NVGRenderer.rect(dx, iy, width, ITEM_HEIGHT, targetBg);
			}

			String optText = fitText(options.get(idx), width - PADDING * 2, Fonts.PRETENDARD_MEDIUM);
			int iTextY = iy + (ITEM_HEIGHT - (int)FONT_SIZE) / 2;
			NVGRenderer.text(optText, dx + PADDING, iTextY, Fonts.PRETENDARD_MEDIUM, textColor, FONT_SIZE);

		}

		if (options.size() > MAX_VISIBLE) {
			int trackH = totalH - 8;
			float thumbRatio = (float) MAX_VISIBLE / options.size();
			int thumbH = Math.max(16, (int)(trackH * thumbRatio));
			float scrollT = (float) scrollOffset / (options.size() - MAX_VISIBLE);
			int thumbY = dy + 4 + (int)((trackH - thumbH) * scrollT);
			NVGRenderer.rect(dx + width - 4, thumbY, 3, thumbH, 0xFF666666, 2f);
		}

		NVGRenderer.popScissor();

		hoveredItem = -1;
		for (int i = 0; i < visibleCount; i++) {
			int idx = i + scrollOffset;
			int iy = dy + 2 + i * ITEM_HEIGHT;
			if (ULayout.isHovered(mouseX, mouseY, dx, iy, width, ITEM_HEIGHT)) {
				hoveredItem = idx;
				break;
			}
		}
		NVGRenderer.pop();
	}

	private String fitText(String text, int maxWidth, LucentFont font) {
		if (NVGRenderer.textWidth(text, font, FONT_SIZE) <= maxWidth) return text;
		String suffix = "...";
		String current = text;
		while (current.length() > 0 && NVGRenderer.textWidth(current + suffix, font, FONT_SIZE) > maxWidth) {
			current = current.substring(0, current.length() - 1);
		}
		return current + suffix;
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (!enabled || !visible || button != 0) return false;

		if (isOpen) {
			int visibleCount = Math.min(options.size(), MAX_VISIBLE);
			int dy = y + height + 2;
			for (int i = 0; i < visibleCount; i++) {
				int idx = i + scrollOffset;
				int iy = dy + 2 + i * ITEM_HEIGHT;
				if (ULayout.isHovered(mouseX, mouseY, x, iy, width, ITEM_HEIGHT)) {
					selectIndex(idx);
					close();
					return true;
				}
			}
			if (!isMouseOver(mouseX, mouseY)) {
				close();
				return false;
			}
		}

		if (isMouseOver(mouseX, mouseY)) {
			if (isOpen) close(); else open();
			return true;
		}
		return false;
	}

	@Override
	public boolean mouseScrolled(double mouseX, double mouseY, double hAmt, double vAmt) {
		if (!isOpen) return false;
		int dy = y + height + 2;
		int totalH = Math.min(options.size(), MAX_VISIBLE) * ITEM_HEIGHT;
		if (ULayout.isHovered(mouseX, mouseY, x, dy, width, totalH)) {
			scrollOffset = (int) UAnimation.clamp(scrollOffset - (int) vAmt, 0, Math.max(0, options.size() - MAX_VISIBLE));
			return true;
		}
		return false;
	}

	private void open() {
		isOpen = true;
		setFocused(true);
	}

	private void close() {
		isOpen = false;
		setFocused(false);
	}

	private void selectIndex(int idx) {
		if (idx < 0 || idx >= options.size()) return;
		this.selectedIndex = idx;
		if (onChange != null) onChange.accept(options.get(idx));
		if (onChangeIndex != null) onChangeIndex.accept(idx);
	}

	public String getValue() {
		return options.isEmpty() ? "" : options.get(selectedIndex);
	}

	public int getSelectedIndex() { return selectedIndex; }

	public void setValue(String value) {
		int idx = options.indexOf(value);
		if (idx >= 0) selectedIndex = idx;
	}

	public void setSelectedIndex(int index) {
		selectedIndex = UAnimation.clamp(index, 0, Math.max(0, options.size() - 1));
	}

	public void setOptions(List<String> options) {
		this.options = new ArrayList<>(options);
		selectedIndex = 0;
		scrollOffset = 0;
	}

	public void addOption(String option) {
		options.add(option);
	}

	public void setOnChange(Consumer<String> onChange) {
		this.onChange = onChange;
	}

	public void setOnChangeIndex(Consumer<Integer> onChangeIndex) {
		this.onChangeIndex = onChangeIndex;
	}

	public boolean isOpen() {
		return isOpen;
	}
}