package silence.simsool.lucent.general.models.data;

import java.lang.reflect.Field;

import silence.simsool.lucent.general.models.abstracts.Mod;

public class KeyBindFieldInfo {
	public final Mod module;
	public final Field field;
	public KeyBindFieldInfo(Mod module, Field field) {
		this.module = module;
		this.field = field;
	}
	public KeyBind getKeyBind() {
		try {
			return (KeyBind) field.get(module);
		} catch (Exception e) {
			return null;
		}
	}
}