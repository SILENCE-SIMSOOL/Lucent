package silence.simsool.lucent.guide;

import static silence.simsool.lucent.Lucent.mc;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenMouseEvents;
import net.fabricmc.loader.api.FabricLoader;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.mixin.accessors.KeyMappingAccessor;
import silence.simsool.lucent.events.impl.LucentEvent;
import silence.simsool.lucent.general.utils.L10n;
import silence.simsool.lucent.general.utils.useful.UScreen;

/** One process-wide coordinator shared by every mod using Lucent. */
public final class GuideManager {
	private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("lucent/guides.properties");
	private static final Properties progress = new Properties();
	private static final GuideQueue queue = new GuideQueue(progress);
	private static final Set<String> signals = new HashSet<>();
	private static boolean initialized;
	private static boolean storageError;
	private static int readyTicks;
	private static int stepTicks;
	private static int cooldown;
	private static boolean continueQueue;

	private GuideManager() {}

	public static void register(Guide guide) {
		queue.register(guide);
	}

	/** Register before other screen input listeners, so guide controls cannot click through. */
	public static void init() {
		if (initialized) return;
		initialized = true;
		try {
			progress.putAll(GuideProgress.load(FILE));
		} catch (IOException | IllegalArgumentException exception) {
			storageError = true;
			Lucent.LOG.error("Cannot load guide progress: " + exception.getMessage());
		}
		ScreenEvents.BEFORE_INIT.register((client, screen, width, height) -> {
			ScreenEvents.afterRender(screen).register((s, graphics, mx, my, delta) -> GuideOverlay.render(graphics));
			ScreenMouseEvents.allowMouseClick(screen).register((s, click) ->
				!GuideOverlay.click(click.x(), click.y(), click.button()));
			ScreenMouseEvents.allowMouseRelease(screen).register((s, click) ->
				!GuideOverlay.release(click.x(), click.y()));
			ScreenKeyboardEvents.allowKeyPress(screen).register((s, input) ->
				!GuideOverlay.key(input.key(), input.modifiers()));
		});
		LucentEvent.TICK_EVENT.register(GuideManager::tick);
	}

	public static Guide active() { return queue.active(); }
	public static GuideStep step() { return queue.step(); }
	public static int index() { return queue.index(); }
	public static boolean storageError() { return storageError; }
	public static boolean inWorld() { return mc.player != null && mc.level != null; }

	public static void signal(String event) {
		if (inWorld() && active() != null) signals.add(event);
	}

	public static boolean signaled(String event) {
		return signals.contains(event);
	}

	public static void next() {
		if (!inWorld() || step() == null || step().completed() != null || !step().visible().getAsBoolean()) return;
		advance();
	}

	public static void skip() {
		if (!inWorld() || !queue.skip()) return;
		changed(true);
	}

	public static void open() {
		if (inWorld() && step() != null && step().open() != null
				&& (step().actionWhileVisible() || !step().visible().getAsBoolean())) step().open().run();
	}

	private static void tick() {
		if (!inWorld()) {
			readyTicks = 0;
			return;
		}
		if (++readyTicks < 20) return;
		if (cooldown > 0) { cooldown--; return; }
		if (active() == null) {
			// Do not interrupt loading, chat or an unrelated screen.
			if (!continueQueue && UScreen.getScreen() != null) return;
			if (!queue.startNext()) return;
			if (continueQueue) UScreen.setScreen(new GuideScreen());
			continueQueue = false;
			stepTicks = 0;
			signals.clear();
		}
		if (UScreen.getScreen() == null) UScreen.setScreen(new GuideScreen());
		GuideStep current = step();
		if (++stepTicks >= 4 && current.completed() != null && current.visible().getAsBoolean()
				&& current.completed().getAsBoolean()) {
			advance();
		}
	}

	private static void advance() {
		changed(queue.advance());
	}

	private static void changed(boolean finished) {
		signals.clear();
		stepTicks = 0;
		GuideOverlay.resetFocus();
		if (!finished) return;
		try {
			GuideProgress.save(FILE, progress);
			storageError = false;
		} catch (IOException exception) {
			storageError = true;
			Lucent.LOG.error("Cannot save guide progress: " + exception.getMessage());
		}
		// Never start the next guide over a screen owned by the previous guide.
		if (UScreen.getScreen() instanceof GuideScreen) UScreen.setScreen(null);
		cooldown = 10;
		continueQueue = true;
	}

	public static String text(String key) {
		L10n.load();
		return L10n.translate(key).replace("{menuKey}", ((KeyMappingAccessor) Lucent.CONFIG_KEY).getKey().getDisplayName().getString());
	}
}
