package silence.simsool.lucent.general.managers.impl;

import java.util.ArrayList;
import java.util.List;

import silence.simsool.lucent.events.impl.LucentEvent;

public class TaskManager {

	private static final List<Task> tasks = new ArrayList<>();
	private static final List<Task> serverTasks = new ArrayList<>();

	public static void register() {
		LucentEvent.TICK_EVENT.register(() -> {
			synchronized (tasks) {
				if (tasks.isEmpty()) return;

				tasks.removeIf(task -> {
					if (task.delay <= 0) {
						task.callback.run();
						return true;
					}
					task.delay--;
					return false;
				});
			}
		});

		LucentEvent.SERVER_TICK_EVENT.register(() -> {
			synchronized (serverTasks) {
				if (serverTasks.isEmpty()) return;

				serverTasks.removeIf(task -> {
					if (task.delay <= 0) {
						task.callback.run();
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

	public static void scheduleServerTask(Runnable callback) {
		scheduleServerTask(0, callback);
	}

	public static void scheduleServerTask(int delay, Runnable callback) {
		synchronized (serverTasks) {
			serverTasks.add(new Task(delay, callback));
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