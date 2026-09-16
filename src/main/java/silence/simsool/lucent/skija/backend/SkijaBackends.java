package silence.simsool.lucent.skija.backend;

import com.mojang.blaze3d.systems.GpuDevice;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vulkan.VulkanDevice;

import silence.simsool.lucent.mixin.accessors.GpuDeviceAccessor;

public class SkijaBackends {

	private static SkijaBackend activeBackend = null;

	public static SkijaBackend getActive() {
		if (activeBackend == null) {
			try {
				GpuDevice device = RenderSystem.getDevice(); if (device == null) return null;

				if (((GpuDeviceAccessor) device).getBackend() instanceof VulkanDevice) {
					activeBackend = VulkanSkijaBackend.INSTANCE;
				}
				else activeBackend = GlSkijaBackend.INSTANCE;

			} catch (Throwable ignored) {
				activeBackend = GlSkijaBackend.INSTANCE;
			}
		}
		return activeBackend;
	}

	public static void reset() {
		if (activeBackend != null) {
			activeBackend.dispose();
			activeBackend = null;
		}
	}

}