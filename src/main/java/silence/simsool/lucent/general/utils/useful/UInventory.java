package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import net.minecraft.world.inventory.AbstractContainerMenu;

public class UInventory {

	public static AbstractContainerMenu getContainer() {
		return mc.player != null ? mc.player.containerMenu : null;
	}

	public static int getContainerId() {
		return mc.player != null ? mc.player.containerMenu.containerId : -1;
	}

	public static int getInventorySize() {
		if (mc.player != null && mc.player.containerMenu != null) {
			return mc.player.containerMenu.slots.size();
		}
		return 0;
	}

	public static boolean isChestItem(int slotNumber) {
		int size = getInventorySize();
		if (size == 0) return false;
		return slotNumber < (size - 36);
	}

	public static boolean isChestItem(int inventorySize, int slotNumber) {
		return slotNumber < (inventorySize - 36);
	}

}