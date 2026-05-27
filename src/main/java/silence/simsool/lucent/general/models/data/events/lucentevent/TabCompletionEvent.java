package silence.simsool.lucent.general.models.data.events.lucentevent;

import java.util.ArrayList;
import silence.simsool.lucent.events.impl.LucentEvent;

public class TabCompletionEvent {

	private final String fullInput;
	private final String beforeCursor;
	private final ArrayList<String> existing;
	private String[] additional;

	public TabCompletionEvent(String fullInput, String beforeCursor, ArrayList<String> existing) {
		this.fullInput = fullInput;
		this.beforeCursor = beforeCursor;
		this.existing = existing;
	}

	public void post() {
		LucentEvent.TAB_COMPLETION_EVENT.invoker().onTabComplete(this);
	}

	public String[] intoSuggestionArray() {
		return additional;
	}

	public void setAdditional(String[] additional) {
		this.additional = additional;
	}

	public String getFullInput() {
		return fullInput;
	}

	public String getBeforeCursor() {
		return beforeCursor;
	}

	public ArrayList<String> getExisting() {
		return existing;
	}

}