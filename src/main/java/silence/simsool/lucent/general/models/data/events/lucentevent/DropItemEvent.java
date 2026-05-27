package silence.simsool.lucent.general.models.data.events.lucentevent;

import net.minecraft.world.item.ItemStack;
import silence.simsool.lucent.general.enums.DropType;

public class DropItemEvent {

	public final ItemStack stack;
	public final DropType dropType;
	public final boolean all;
	private boolean canceled = false;

	public DropItemEvent(ItemStack stack, DropType dropType, boolean all) {
		this.stack = stack;
		this.dropType = dropType;
		this.all = all;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}