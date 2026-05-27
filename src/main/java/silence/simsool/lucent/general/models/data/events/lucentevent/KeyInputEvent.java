package silence.simsool.lucent.general.models.data.events.lucentevent;

import com.mojang.blaze3d.platform.InputConstants;

public class KeyInputEvent {

	public InputConstants.Key key;
	private boolean canceled = false;

	public KeyInputEvent(InputConstants.Key key) {
		this.key = key;
	}

	public void cancel() {
		this.canceled = true;
	}

	public boolean isCanceled() {
		return canceled;
	}

}