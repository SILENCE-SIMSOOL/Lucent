package silence.simsool.lucent.general.managers.impl;

import java.util.ArrayList;
import java.util.List;

import silence.simsool.lucent.events.impl.LucentEvent;

public class TaskManager {

	private static final List<Task> tasks = new ArrayList<>();
	private static final List<Task> serverTasks = new ArrayList<>();

	public static void register() {
		LucentEvent.TICK_EVENT.register(() -> runTasks(tasks));
		LucentEvent.SERVER_TICK_EVENT.register(() -> runTasks(serverTasks));
	}

	// 락 안에서는 만료 작업만 분리하고, 콜백은 락 밖에서 실행해 재등록 시 리스트 변경 충돌을 방지
	private static void runTasks(List<Task> list) {
		List<Task> due = null;

		synchronized (list) {
			if (list.isEmpty()) return;

			int remaining = 0;
			int size = list.size();
			for (int i = 0; i < size; i++) {
				Task task = list.get(i);
				if (task.delay <= 0) {
					if (due == null) due = new ArrayList<>();
					due.add(task);
				} else {
					task.delay--;
					if (remaining != i) list.set(remaining, task);
					remaining++;
				}
			}
			if (remaining != size) list.subList(remaining, size).clear();
		}

		if (due == null) return;
		for (Task task : due) task.callback.run();
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