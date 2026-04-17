package silence.simsool.lucent.general.utils.useful;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UThread {

	public static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor(r -> {
		Thread thread = new Thread(r, "LUCENT-THREAD");
		thread.setDaemon(true);
		return thread;
	});

	public static ExecutorService createThread(String name) {
		return Executors.newSingleThreadExecutor(r -> {
			Thread thread = new Thread(r, name);
			thread.setDaemon(true);
			return thread;
		});
	}

}