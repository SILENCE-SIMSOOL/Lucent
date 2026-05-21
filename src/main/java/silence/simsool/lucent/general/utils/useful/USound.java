package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.sounds.SoundEvent;

public class USound {

	public static void playSound(SoundEvent sound) {
		if (mc.player != null) {
			mc.player.playSound(sound);
		}
	}

	public static void playSound(SoundEvent sound, float volume, float pitch) {
		if (mc.player != null) {
			mc.player.playSound(sound, volume, pitch);
		}
	}

}