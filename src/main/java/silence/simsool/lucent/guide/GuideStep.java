package silence.simsool.lucent.guide;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

/** Translation keys are resolved each frame so guides follow the selected language. */
public record GuideStep(String title, String body, BooleanSupplier completed,
		BooleanSupplier visible, Supplier<GuideTarget> target, Runnable open, boolean actionWhileVisible) {
	public static GuideStep explain(String title, String body) {
		return new GuideStep(title, body, null, () -> true, () -> null, null, false);
	}

	public static GuideStep await(String title, String body, BooleanSupplier completed,
			BooleanSupplier visible, Supplier<GuideTarget> target, Runnable open) {
		return new GuideStep(title, body, completed, visible, target, open, true);
	}

	/** Observe the user's action; the button only reopens a missing screen. */
	public static GuideStep observe(String title, String body, BooleanSupplier completed,
			BooleanSupplier visible, Supplier<GuideTarget> target, Runnable reopen) {
		return new GuideStep(title, body, completed, visible, target, reopen, false);
	}
}
