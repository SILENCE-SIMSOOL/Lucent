# UI Widgets Guide

Lucent provides a rich collection of modular GUI controls to construct interactive interfaces. Every widget automatically manages its own drawing operations, input events, and animation interpolation frames.

All components inherit from the parent class [UIWidget](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/ui/widget/UIWidget.java).

---

## 1. Available Widgets

### ActionButton
A standard clickable button component that executes a defined `Runnable` callback action upon click events.
- **Source Link**: [ActionButton.java](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/ActionButton.java)
- **Use Case**: Running actions, reset configurations, or launching sub-menus.

---

### ToggleButton
An animated slide switch control representing boolean `true`/`false` states.
- **Source Link**: [ToggleButton.java](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/ToggleButton.java)
- **Use Case**: Enabling/disabling specific mod features.

---

### Slider
A linear bar control that lets the user choose numeric values by clicking and dragging a handle along an axis (handles `int`, `float`, `double`).
- **Source Link**: [Slider.java](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/Slider.java)
- **Use Case**: Opacity sliders, multiplier scales, size attributes.

---

### Selector
A dropdown cycle widget enabling selections from a predefined array of string options.
- **Source Link**: [Selector.java](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/Selector.java)
- **Use Case**: Choosing animation styles, UI layouts, or enum settings.

---

### TextBox
A text entry field supporting keystroke capture, character limit validation, and selection operations.
- **Source Link**: [TextBox.java](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/TextBox.java)
- **Use Case**: Inputting player usernames, API keys, custom strings, or filter terms.

---

### KeyBindButton
A utility button designed to record game-control keystrokes (keyboard bindings or mouse clicks).
- **Source Link**: [KeyBindButton.java](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/KeyBindButton.java)
- **Use Case**: Binding shortcut hotkeys to toggle modules or toggle overlays.

---

### ColorPicker & ColorPickerButton
- **ColorPicker**: A complete HSVA graphical palette tool with fine saturation, hue, and opacity control sliders and Hex string input fields.
- **ColorPickerButton**: A small button displaying a colored square swatch. Clicking it opens a popup window containing the full `ColorPicker` canvas.
- **Source Links**:
	- [ColorPicker.java](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/color/ColorPicker.java)
	- [ColorPickerButton.java](file:///d:/FROZEN/Dev%20Mod/Mod-Build/%21Fabric/Projects/Lucent/src/main/java/silence/simsool/lucent/ui/widget/components/color/ColorPickerButton.java)
- **Use Case**: Selecting themes, HUD text colors, or outline highlights.

---

## 2. Basic Widget Usage

To draw and capture input for a standalone widget in your custom GUI:

```java
import silence.simsool.lucent.ui.widget.components.ToggleButton;

public class MyCustomScreen extends Screen {
	private ToggleButton myToggle;

	@Override
	protected void init() {
		// Initialize the widget (x, y, width, height)
		myToggle = new ToggleButton(50, 50, 40, 20);
		myToggle.setOn(true); // set initial state
	}

	@Override
	public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
		// Widgets are drawn inside the NanoVG PIP block
		NVGPIPRenderer.draw(graphics, 0, 0, width, height, () -> {
			myToggle.draw(mouseX, mouseY, delta);
		});
	}

	@Override
	public boolean mouseClicked(double mouseX, double mouseY, int button) {
		if (myToggle.mouseClicked(mouseX, mouseY, button)) {
			boolean isActivated = myToggle.isOn();
			// Handle state changes
			return true;
		}
		return super.mouseClicked(mouseX, mouseY, button);
	}
}
```
Each widget handles mouse clicks, key events, focus status, and hover animations autonomously.
