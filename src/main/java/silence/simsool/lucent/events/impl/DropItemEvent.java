package silence.simsool.lucent.events.impl;

import static net.fabricmc.fabric.api.event.EventFactory.createArrayBacked;

import net.fabricmc.fabric.api.event.Event;
import net.minecraft.world.item.ItemStack;
import silence.simsool.lucent.general.enums.DropType;

public final class DropItemEvent {

	public static class DropItem {
		public final ItemStack stack;
		public final DropType dropType;
		public final boolean all;
		private boolean canceled = false;

		public DropItem(ItemStack stack, DropType dropType, boolean all) {
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

	public static final Event<Handler> EVENT = createArrayBacked(
		Handler.class, listeners -> event -> {
			for (Handler listener : listeners) {
				listener.onDropItem(event);
				if (event.isCanceled()) break;
			}
		}
	);

	@FunctionalInterface
	public interface Handler {
		void onDropItem(DropItem event);
	}

}