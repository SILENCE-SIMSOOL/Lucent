package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;
import java.lang.reflect.Field;
import net.fabricmc.fabric.api.event.Event;
import silence.simsool.lucent.general.models.abstracts.Mod;
import silence.simsool.lucent.general.models.data.KeyBind;
import silence.simsool.lucent.general.models.interfaces.events.configevent.*;

public class ConfigEvent {

	public static class ToggleButtonEvent {
		private final Mod mod;
		private final Field field;
		private final String configName;
		private final boolean oldValue;
		private final boolean newValue;

		public ToggleButtonEvent(Mod mod, Field field, String configName, boolean oldValue, boolean newValue) {
			this.mod = mod;
			this.field = field;
			this.configName = configName;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public Mod getMod() {
			return mod;
		}

		public Field getField() {
			return field;
		}

		public String getConfigName() {
			return configName;
		}

		public boolean getOldValue() {
			return oldValue;
		}

		public boolean getNewValue() {
			return newValue;
		}
	}

	public static final Event<IToggleButtonEvent> TOGGLE_BUTTON = createArrayBacked(
		IToggleButtonEvent.class, listeners -> event -> {
			for (IToggleButtonEvent listener : listeners) listener.onChange(event);
		}
	);

	public static class SliderEvent {
		private final Mod mod;
		private final Field field;
		private final String configName;
		private final double oldValue;
		private final double newValue;

		public SliderEvent(Mod mod, Field field, String configName, double oldValue, double newValue) {
			this.mod = mod;
			this.field = field;
			this.configName = configName;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public Mod getMod() { return mod; }
		public Field getField() { return field; }
		public String getConfigName() { return configName; }
		public double getOldValue() { return oldValue; }
		public double getNewValue() { return newValue; }
	}

	public static final Event<ISliderEvent> SLIDER = createArrayBacked(
		ISliderEvent.class, listeners -> event -> {
			for (ISliderEvent listener : listeners) listener.onChange(event);
		}
	);

	public static class SelectorEvent {
		private final Mod mod;
		private final Field field;
		private final String configName;
		private final String oldValue;
		private final String newValue;

		public SelectorEvent(Mod mod, Field field, String configName, String oldValue, String newValue) {
			this.mod = mod;
			this.field = field;
			this.configName = configName;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public Mod getMod() {
			return mod;
		}

		public Field getField() {
			return field;
		}

		public String getConfigName() {
			return configName;
		}

		public String getOldValue() {
			return oldValue;
		}

		public String getNewValue() {
			return newValue;
		}
	}

	public static final Event<ISelectorEvent> SELECTOR = createArrayBacked(
		ISelectorEvent.class, listeners -> event -> {
			for (ISelectorEvent listener : listeners) listener.onChange(event);
		}
	);

	public static class ColorPickerEvent {
		private final Mod mod;
		private final Field field;
		private final String configName;
		private final int oldValue;
		private final int newValue;

		public ColorPickerEvent(Mod mod, Field field, String configName, int oldValue, int newValue) {
			this.mod = mod;
			this.field = field;
			this.configName = configName;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public Mod getMod() { return mod; }
		public Field getField() { return field; }
		public String getConfigName() { return configName; }
		public int getOldValue() { return oldValue; }
		public int getNewValue() { return newValue; }
	}

	public static final Event<IColorPickerEvent> COLOR_PICKER = createArrayBacked(
		IColorPickerEvent.class, listeners -> event -> {
			for (IColorPickerEvent listener : listeners) listener.onChange(event);
		}
	);

	public static class TextBoxEvent {
		private final Mod mod;
		private final Field field;
		private final String configName;
		private final String oldValue;
		private final String newValue;

		public TextBoxEvent(Mod mod, Field field, String configName, String oldValue, String newValue) {
			this.mod = mod;
			this.field = field;
			this.configName = configName;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public Mod getMod() {
			return mod;
		}

		public Field getField() {
			return field;
		}

		public String getConfigName() {
			return configName;
		}

		public String getOldValue() {
			return oldValue;
		}

		public String getNewValue() {
			return newValue;
		}
	}

	public static final Event<ITextBoxEvent> TEXT_BOX = createArrayBacked(
		ITextBoxEvent.class, listeners -> event -> {
			for (ITextBoxEvent listener : listeners) listener.onChange(event);
		}
	);

	public static class KeyBindEvent {
		private final Mod mod;
		private final Field field;
		private final String configName;
		private final KeyBind oldValue;
		private final KeyBind newValue;

		public KeyBindEvent(Mod mod, Field field, String configName, KeyBind oldValue, KeyBind newValue) {
			this.mod = mod;
			this.field = field;
			this.configName = configName;
			this.oldValue = oldValue;
			this.newValue = newValue;
		}

		public Mod getMod() {
			return mod;
		}

		public Field getField() {
			return field;
		}

		public String getConfigName() {
			return configName;
		}

		public KeyBind getOldValue() {
			return oldValue;
		}

		public KeyBind getNewValue() {
			return newValue;
		}
	}

	public static final Event<IKeyBindEvent> KEY_BIND = createArrayBacked(
		IKeyBindEvent.class, listeners -> event -> {
			for (IKeyBindEvent listener : listeners) listener.onChange(event);
		}
	);

}