package silence.simsool.lucent.guide;

import java.util.List;

/** Lower priorities run first; IDs break ties independently of mod load order. */
public record Guide(String id, String title, int priority, List<GuideStep> steps) {
	public Guide {
		if (id == null || id.isBlank() || steps.isEmpty()) throw new IllegalArgumentException("A guide needs an ID and steps");
		steps = List.copyOf(steps);
	}
}
