package silence.simsool.lucent.general.models.data.events.lucentevent;

import net.minecraft.world.BossEvent;

public class BossBarRenderEvent {

	public final BossEvent bossBar;
	private boolean canceled = false;

	public BossBarRenderEvent(BossEvent bossBar) {
		this.bossBar = bossBar;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}