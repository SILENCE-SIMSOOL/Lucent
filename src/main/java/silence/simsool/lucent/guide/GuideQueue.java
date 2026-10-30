package silence.simsool.lucent.guide;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/** Client-thread state machine; world transitions suspend rather than finish a guide. */
public final class GuideQueue {

	private final Map<String, Guide> guides = new LinkedHashMap<>();
	private final Properties progress;
	private Guide active;
	private int index;

	public GuideQueue(Properties progress) {
		this.progress = progress;
	}

	public void register(Guide guide) {
		if (guides.putIfAbsent(guide.id(), guide) != null) {
			throw new IllegalArgumentException("Duplicate guide: " + guide.id());
		}
	}

	public boolean startNext() {
		if (active != null) return false;
		active = guides.values().stream()
			.filter(guide -> !progress.containsKey(guide.id()))
			.min(Comparator.comparingInt(Guide::priority).thenComparing(Guide::id))
			.orElse(null);
		index = 0;
		return active != null;
	}

	public Guide active() { return active; }
	public int index() { return index; }
	public GuideStep step() { return active == null ? null : active.steps().get(index); }

	public boolean advance() {
		if (active == null) return false;
		if (++index < active.steps().size()) return false;
		return finish("completed");
	}

	public boolean skip() {
		return active != null && finish("skipped");
	}

	private boolean finish(String status) {
		progress.setProperty(active.id(), status);
		active = null;
		index = 0;
		return true;
	}

}