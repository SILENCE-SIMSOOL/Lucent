package silence.simsool.lucent.client;

import silence.simsool.lucent.general.abstracts.Module;
import silence.simsool.lucent.general.enums.ConfigType;
import silence.simsool.lucent.general.interfaces.ModConfig;

public class ChattingMod extends Module {
	@ModConfig(
		type = ConfigType.SWITCH,
		name = "Remove Chat Background",
		description = "채팅창 배경을 지웁니다.",
		category = "General"
	)
	public boolean removeChatBackground = true;

	@ModConfig(
		type = ConfigType.SLIDER,
		name = "Chat Opacity",
		description = "투명도 조절",
		category = "General",
		min = 0.0,
		max = 1.0,
		step = 0.1
	)
	public double chatOpacity = 0.8;

	public ChattingMod() {
		super("Chatting Mod", "채팅 관련 여러 편의 기능을 제공합니다.", "QOL", "minecraft, chat", "/assets/lucent/textures/modicons/chatmod.png");
	}
}