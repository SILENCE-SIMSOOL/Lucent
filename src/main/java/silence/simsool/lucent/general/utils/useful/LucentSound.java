package silence.simsool.lucent.general.utils.useful;

import static silence.simsool.lucent.Lucent.mc;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import javax.sound.sampled.AudioFormat;

import org.lwjgl.openal.AL10;

import net.minecraft.client.sounds.JOrbisAudioStream;
import net.minecraft.sounds.SoundSource;
import silence.simsool.lucent.Lucent;
import silence.simsool.lucent.general.utils.OSUtils;

public class LucentSound {

	private final String filename;
	private final String downloadUrl;
	private volatile int alBuffer = 0;
	private volatile int dedicatedSource = 0;
	private volatile boolean loading = false;
	private volatile ByteBuffer cachedPcm = null;
	private volatile AudioFormat cachedFormat = null;

	public LucentSound(String filename, String downloadUrl) {
		this.filename = filename;
		this.downloadUrl = downloadUrl;
	}

	public File getFile() {
		return new File(new File(OSUtils.getLucentDir(), "resources/sounds"), filename);
	}

	public void preloadAsync() {
		CompletableFuture.runAsync(this::ensureLoaded);
	}

	public void play() {
		play(1.0f, 1.0f);
	}

	public void play(float pitch) {
		play(1.0f, pitch);
	}

	public void play(float volume, float pitch) {
		if (alBuffer == 0) {
			if (cachedPcm != null && cachedFormat != null) {
				uploadToAl();
			} else {
				ensureLoaded();
				if (cachedPcm != null && cachedFormat != null) {
					uploadToAl();
				}
			}
		}

		if (alBuffer == 0) return;

		float masterVol = 1.0f;
		float uiVol = 1.0f;
		if (mc != null && mc.options != null) {
			masterVol = mc.options.getSoundSourceVolume(SoundSource.MASTER);
			uiVol = mc.options.getSoundSourceVolume(SoundSource.UI);
		}
		float finalVol = volume * masterVol * uiVol;
		if (finalVol <= 0.001f) return;

		if (mc != null && !mc.isSameThread()) {
			mc.execute(() -> playInternal(finalVol, pitch));
		} else {
			playInternal(finalVol, pitch);
		}
	}

	private synchronized void playInternal(float volume, float pitch) {
		if (alBuffer == 0) return;
		if (dedicatedSource == 0) {
			int[] src = new int[1];
			AL10.alGenSources(src);
			dedicatedSource = src[0];
		}
		if (dedicatedSource == 0) return;

		AL10.alSourceStop(dedicatedSource);
		AL10.alSourcei(dedicatedSource, AL10.AL_BUFFER, alBuffer);
		AL10.alSourcef(dedicatedSource, AL10.AL_GAIN, volume);
		AL10.alSourcef(dedicatedSource, AL10.AL_PITCH, pitch);
		AL10.alSourcePlay(dedicatedSource);
	}

	private synchronized void uploadToAl() {
		if (alBuffer != 0 || cachedPcm == null || cachedFormat == null) return;
		try {
			int formatInt;
			if (cachedFormat.getChannels() == 1) {
				formatInt = (cachedFormat.getSampleSizeInBits() == 8) ? AL10.AL_FORMAT_MONO8 : AL10.AL_FORMAT_MONO16;
			} else {
				formatInt = (cachedFormat.getSampleSizeInBits() == 8) ? AL10.AL_FORMAT_STEREO8 : AL10.AL_FORMAT_STEREO16;
			}
			int buf = AL10.alGenBuffers();
			AL10.alBufferData(buf, formatInt, cachedPcm, (int) cachedFormat.getSampleRate());
			this.alBuffer = buf;
			this.cachedPcm = null;
		} catch (Exception e) {
			Lucent.LOG.error("Failed to upload sound to OpenAL buffer: " + filename + " - " + e.getMessage());
		}
	}

	private synchronized void ensureLoaded() {
		if (alBuffer != 0 || (cachedPcm != null && cachedFormat != null)) return;
		if (loading) return;
		loading = true;

		try {
			File soundFile = getFile();
			if (!soundFile.exists() || soundFile.length() == 0) {
				download(soundFile);
			}

			if (!soundFile.exists() || soundFile.length() == 0) {
				loading = false;
				return;
			}

			try (InputStream in = new BufferedInputStream(new FileInputStream(soundFile))) {
				JOrbisAudioStream stream = new JOrbisAudioStream(in);
				this.cachedPcm = stream.readAll();
				this.cachedFormat = stream.getFormat();
				stream.close();
			}
		} catch (Exception e) {
			Lucent.LOG.error("Failed to decode sound " + filename + ": " + e.getMessage());
		} finally {
			loading = false;
		}
	}

	private void download(File targetFile) {
		try {
			File parent = targetFile.getParentFile();
			if (!parent.exists()) parent.mkdirs();

			String url = downloadUrl;
			if (url.contains("github.com") && url.contains("/blob/")) {
				url = url.replace("github.com", "raw.githubusercontent.com").replace("/blob/", "/");
			}

			File tempFile = new File(parent, targetFile.getName() + ".tmp");
			HttpClient client = HttpClient.newBuilder()
				.connectTimeout(Duration.ofSeconds(10))
				.followRedirects(HttpClient.Redirect.NORMAL)
				.build();
			HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create(url))
				.timeout(Duration.ofSeconds(15))
				.GET()
				.build();

			HttpResponse<Path> response = client.send(request, HttpResponse.BodyHandlers.ofFile(tempFile.toPath()));
			if (response.statusCode() == 200 && tempFile.exists() && tempFile.length() > 0) {
				Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Lucent.LOG.info("Successfully downloaded sound: " + targetFile.getName());
			} else {
				Files.deleteIfExists(tempFile.toPath());
				Lucent.LOG.warn("Failed to download sound " + targetFile.getName() + ", status: " + response.statusCode());
			}
		} catch (Exception e) {
			Lucent.LOG.warn("Error downloading sound " + targetFile.getName() + ": " + e.getMessage());
		}
	}

}