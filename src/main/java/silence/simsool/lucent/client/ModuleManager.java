package silence.simsool.lucent.client;

import silence.simsool.lucent.general.abstracts.Module;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
	public final List<Module> modules = new ArrayList<>();
	private final File configDirectory;

	public ModuleManager(File configDirectory) {
		this.configDirectory = configDirectory;
		if (!this.configDirectory.exists()) {
			this.configDirectory.mkdirs();
		}
	}

	public void register(Module module) {
		modules.add(module);
	}

	public void registerAll() {
		register(new ChattingMod());
	}

	public void loadConfigs() {
	}

	public void saveConfigs() {
	}
}