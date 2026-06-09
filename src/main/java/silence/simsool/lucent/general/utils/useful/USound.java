package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public class USound {

	public static void playSound(SoundEvent sound, float volume) {
		mc.getSoundManager().play(SimpleSoundInstance.forUI(sound, volume));
	}

	public static void playSound(SoundEvent sound, float volume, float pitch) {
		mc.getSoundManager().play(SimpleSoundInstance.forUI(sound, volume, pitch));
	}

	public static void playSoundAt(SoundEvent sound) {
		if (mc.player != null) {
			mc.player.playSound(sound);
		}
	}

	public static void playSoundAt(SoundEvent sound, float volume, float pitch) {
		if (mc.player != null) {
			mc.player.playSound(sound, volume, pitch);
		}
	}

	public static void playSoundAt(SoundEvent sound, SoundSource category, int x, int y, int z, float volume, float pitch, boolean state) {
		if (mc.level != null) {
			mc.level.playLocalSound(x, y, z, sound,	category, volume, pitch, false);
		}
	}

	public static void playSoundAt(SoundEvent sound, SoundSource category, BlockPos pos, float volume, float pitch, boolean state) {
		if (mc.level != null) {
			mc.level.playLocalSound(pos, sound,	category, volume, pitch, false);
		}
	}

}