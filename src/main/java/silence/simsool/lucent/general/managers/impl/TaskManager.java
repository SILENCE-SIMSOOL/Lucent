package silence.simsool.lucent.general.managers.impl;

import static silence.simsool.lucent.Lucent.mc;

import java.util.ArrayList;
import java.util.List;

import silence.simsool.lucent.events.impl.LucentEvent;

public class TaskManager {

	private static final List<Task> tasks = new ArrayList<>();

	public static void register() {
		LucentEvent.TICK_EVENT.register(() -> {
			synchronized (tasks) {
				if (tasks.isEmpty()) return;

				tasks.removeIf(task -> {
					if (task.delay <= 0) {
						mc.execute(task.callback);
						return true;
					}
					task.delay--;
					return false;
				});
			}
		});
	}

	public static void scheduleTask(Runnable callback) {
		scheduleTask(0, callback);
	}

	public static void scheduleTask(int delay, Runnable callback) {
		synchronized (tasks) {
			tasks.add(new Task(delay, callback));
		}
	}

	private static class Task {
		int delay;
		final Runnable callback;

		Task(int delay, Runnable callback) {
			this.delay = delay;
			this.callback = callback;
		}
	}

}