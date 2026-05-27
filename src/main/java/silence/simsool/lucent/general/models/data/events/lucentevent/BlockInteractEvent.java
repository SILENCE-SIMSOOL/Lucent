package silence.simsool.lucent.general.models.data.events.lucentevent;

import net.minecraft.core.BlockPos;

public class BlockInteractEvent {

	public BlockPos pos;
	private boolean canceled = false;

	public BlockInteractEvent(BlockPos pos) {
		this.pos = pos;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}