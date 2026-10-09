package silence.simsool.lucent;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

import io.github.humbleui.skija.Canvas;
import io.github.humbleui.skija.Data;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.Paint;
import io.github.humbleui.skija.Surface;
import io.github.humbleui.types.Rect;
import silence.simsool.lucent.skija.compositor.SkijaCompositor;

public class HudCacheRegression {
	public static void run() throws Exception {
		SkijaCompositor compositor = new SkijaCompositor();
		Object fixed = new Object(), changing = new Object();
		AtomicInteger draws = new AtomicInteger();
		try (Surface actual = Surface.makeRasterN32Premul(64, 64);
			 Surface expected = Surface.makeRasterN32Premul(64, 64)) {
			for (int frame = 0; frame < 4; frame++) {
				int color = frame < 2 ? 0xFFCC3366 : 0xFF22AA44;
				int x = 12 + frame;
				actual.getCanvas().clear(0);
				expected.getCanvas().clear(0);
				compositor.drawCachedHud(fixed, color, 64, 64, () -> {
					draws.incrementAndGet();
					compositor.enqueue(canvas -> panel(canvas, color, 4));
				});
				compositor.drawCachedHud(changing, null, 64, 64, () ->
					compositor.enqueue(canvas -> panel(canvas, 0x801133FF, x)));
				replay(compositor, actual.getCanvas());
				panel(expected.getCanvas(), color, 4);
				panel(expected.getCanvas(), 0x801133FF, x);
				check(Arrays.equals(pixels(actual), pixels(expected)), "Mixed HUD pixels differ on frame " + frame);
			}
			check(draws.get() == 2, "Unchanged HUD was rebuilt");
			// A pending cached draw must survive switching that HUD to uncached drawing.
			compositor.drawCachedHud(fixed, 0xFF22AA44, 64, 64, () -> {
				throw new AssertionError("Cache miss");
			});
			compositor.drawCachedHud(fixed, null, 64, 64, () ->
				compositor.enqueue(canvas -> panel(canvas, 0xFFEEEEEE, 0)));
			replay(compositor, actual.getCanvas());
			check(compositor.size() == 0, "Batch retained completed commands");
		} finally {
			compositor.shutdown();
		}
		System.out.println("PASS: mixed static/dynamic HUD pixels, cache reuse, key invalidation, pending cache disposal");
	}

	private static void panel(Canvas canvas, int color, int x) {
		try (Paint paint = new Paint()) {
			paint.setColor(color);
			int saved = canvas.save();
			canvas.translate(x, 3);
			canvas.clipRect(Rect.makeWH(30, 27));
			canvas.drawRect(Rect.makeXYWH(0, 0, 34, 34), paint);
			canvas.restoreToCount(saved);
		}
	}

	@SuppressWarnings("unchecked")
	private static void replay(SkijaCompositor compositor, Canvas canvas) throws Exception {
		Field field = SkijaCompositor.class.getDeclaredField("batch");
		field.setAccessible(true);
		List<Consumer<Canvas>> commands = (List<Consumer<Canvas>>) field.get(compositor);
		for (Consumer<Canvas> command : commands) command.accept(canvas);
		compositor.discard();
	}

	private static byte[] pixels(Surface surface) {
		try (Image image = surface.makeImageSnapshot(); Data data = image.encodeToData()) {
			return data.getBytes();
		}
	}

	private static void check(boolean condition, String message) {
		if (!condition) throw new AssertionError(message);
	}
}
