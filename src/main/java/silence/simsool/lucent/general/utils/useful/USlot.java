package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.network.HashedStack;
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket;
import net.minecraft.world.inventory.ContainerInput;
import silence.simsool.lucent.general.utils.ClientHandler;

public class USlot {

	public static void clickSlot(int containerId, int slotIndex, int button, ContainerInput clickType) {
		if (mc.gameMode == null || mc.player == null) return;
		mc.gameMode.handleContainerInput(containerId, slotIndex, button, clickType, mc.player);
	}

	public static void leftClick(int containerId, int slotIndex) {
		clickSlot(containerId, slotIndex, 0, ContainerInput.PICKUP);
	}

	public static void rightClick(int containerId, int slotIndex) {
		clickSlot(containerId, slotIndex, 1, ContainerInput.PICKUP);
	}

	public static void dropClick(int containerId, int slotIndex) {
		clickSlot(containerId, slotIndex, 0, ContainerInput.THROW);
	}

	public static void shiftClick(int containerId, int slotIndex) {
		clickSlot(containerId, slotIndex, 0, ContainerInput.QUICK_MOVE);
	}

	public static void leftClickClose(int containerId, int slotIndex) {
		clickSlot(containerId, slotIndex, 0, ContainerInput.PICKUP);
		UScreen.close();
	}

	public static void rightClickClose(int containerId, int slotIndex) {
		clickSlot(containerId, slotIndex, 1, ContainerInput.PICKUP);
		UScreen.close();
	}

	public static void dropClickClose(int containerId, int slotIndex) {
		clickSlot(containerId, slotIndex, 0, ContainerInput.THROW);
		UScreen.close();
	}

	public static void shiftClickClose(int containerId, int slotIndex) {
		clickSlot(containerId, slotIndex, 0, ContainerInput.QUICK_MOVE);
		UScreen.close();
	}

	public static void sendClickPacket(int containerId, int slotIndex, int button, ContainerInput clickType) {
		if (mc.player == null) return;
		ServerboundContainerClickPacket packet = new ServerboundContainerClickPacket(
			containerId,
			mc.player.containerMenu.getStateId(),
			(short) slotIndex,
			(byte) button,
			clickType,
			Int2ObjectMaps.emptyMap(),
			HashedStack.EMPTY // 마우스 커서에 아이템이 안 들리도록 하기 위함
		);
		ClientHandler.sendPacket(packet);
	}

	public static void leftClickSCP(int containerId, int slotIndex) {
		sendClickPacket(containerId, slotIndex, 0, ContainerInput.PICKUP);
	}

	public static void rightClickSCP(int containerId, int slotIndex) {
		sendClickPacket(containerId, slotIndex, 1, ContainerInput.PICKUP);
	}

	public static void dropClickSCP(int containerId, int slotIndex) {
		sendClickPacket(containerId, slotIndex, 0, ContainerInput.THROW);
	}

	public static void shiftClickSCP(int containerId, int slotIndex) {
		sendClickPacket(containerId, slotIndex, 0, ContainerInput.QUICK_MOVE);
	}

	public static void leftClickSCPClose(int containerId, int slotIndex) {
		sendClickPacket(containerId, slotIndex, 0, ContainerInput.PICKUP);
		UScreen.close();
	}

	public static void rightClickSCPClose(int containerId, int slotIndex) {
		sendClickPacket(containerId, slotIndex, 1, ContainerInput.PICKUP);
		UScreen.close();
	}

	public static void dropClickSCPClose(int containerId, int slotIndex) {
		sendClickPacket(containerId, slotIndex, 0, ContainerInput.THROW);
		UScreen.close();
	}

	public static void shiftClickSCPClose(int containerId, int slotIndex) {
		sendClickPacket(containerId, slotIndex, 0, ContainerInput.QUICK_MOVE);
		UScreen.close();
	}

}