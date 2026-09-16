package silence.simsool.lucent.skija.backend;

import org.lwjgl.vulkan.VK;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK12;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkPhysicalDevice;
import org.lwjgl.vulkan.VkQueue;

import com.mojang.blaze3d.GpuFormat;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vulkan.VulkanConst;
import com.mojang.blaze3d.vulkan.VulkanDevice;
import com.mojang.blaze3d.vulkan.VulkanGpuTexture;
import com.mojang.blaze3d.vulkan.VulkanQueue;

import io.github.humbleui.skija.BackendRenderTarget;
import io.github.humbleui.skija.BackendTexture;
import io.github.humbleui.skija.ColorAlphaType;
import io.github.humbleui.skija.ColorSpace;
import io.github.humbleui.skija.ColorType;
import io.github.humbleui.skija.DirectContext;
import io.github.humbleui.skija.Image;
import io.github.humbleui.skija.Surface;
import io.github.humbleui.skija.SurfaceOrigin;
import io.github.humbleui.skija.VkImageInfo;
import io.github.humbleui.skija.VulkanAlloc;
import silence.simsool.lucent.mixin.accessors.GpuDeviceAccessor;

public class VulkanSkijaBackend implements SkijaBackend {

	public static final VulkanSkijaBackend INSTANCE = new VulkanSkijaBackend();

	private static final int VK_IMAGE_TILING_OPTIMAL = 0;
	private static final int VK_IMAGE_LAYOUT_GENERAL = 1;
	private static final int VK_USAGE_TRANSFER_SRC = 0x1;
	private static final int VK_USAGE_SAMPLED = 0x4;
	private static final int VK_QUEUE_FAMILY_IGNORED = -1;
	private static final int VK_SHARING_MODE_EXCLUSIVE = 0;

	private DirectContext cachedContext = null;
	private VulkanFrameBarrier barrier = null;

	@Override
	public String getDisplayName() {
		return "Vulkan";
	}

	@Override
	public DirectContext getContext() {
		if (cachedContext == null) {
			cachedContext = createContext();
		}
		return cachedContext;
	}

	private DirectContext createContext() {
		VulkanDevice device = (VulkanDevice) ((GpuDeviceAccessor) RenderSystem.getDevice()).getBackend();
		VkInstance vkInstance = device.instance().vkInstance();
		VkDevice vkDevice = device.vkDevice();
		VkPhysicalDevice vkPhysicalDevice = vkDevice.getPhysicalDevice();
		VulkanQueue queue = device.graphicsQueue();
		VkQueue vkQueue = queue.vkQueue();

		barrier = new VulkanFrameBarrier(vkDevice, vkQueue, queue.queueFamilyIndex());

		long getInstanceProcAddr = VK.getFunctionProvider().getFunctionAddress("vkGetInstanceProcAddr");
		long getDeviceProcAddr = VK10.vkGetInstanceProcAddr(vkInstance, "vkGetDeviceProcAddr");

		return DirectContext.makeVulkan(
				vkInstance.address(),
				vkPhysicalDevice.address(),
				vkDevice.address(),
				vkQueue.address(),
				queue.queueFamilyIndex(),
				getInstanceProcAddr,
				getDeviceProcAddr,
				VK12.VK_API_VERSION_1_2
		);
	}

	@Override
	public void onBeginFrame() {}

	@Override
	public Surface wrapTarget(GpuTextureView view) {
		VulkanGpuTexture texture = (VulkanGpuTexture) view.texture();
		GpuFormat format = texture.getFormat();
		BackendRenderTarget renderTarget = BackendRenderTarget.makeVulkan(
				view.getWidth(0),
				view.getHeight(0),
				texture.vkImage(),
				VK_IMAGE_TILING_OPTIMAL,
				VK_IMAGE_LAYOUT_GENERAL,
				VulkanConst.toVk(format),
				VulkanConst.textureUsageToVk(texture.usage(), format) | VK_USAGE_TRANSFER_SRC,
				1,
				1
		);
		return Surface.wrapBackendRenderTarget(
				getContext(), renderTarget, SurfaceOrigin.TOP_LEFT, ColorType.RGBA_8888, ColorSpace.getSRGB()
		);
	}

	@Override
	public Image wrapTexture(GpuTextureView view, boolean premultiplied) {
		if (!(view.texture() instanceof VulkanGpuTexture texture)) return null;
		GpuFormat format = texture.getFormat();
		VulkanAlloc fakeAlloc = new VulkanAlloc(0L, 0L, 0L, 0);

		VkImageInfo info = new VkImageInfo(
				texture.vkImage(),
				fakeAlloc,
				VK_IMAGE_TILING_OPTIMAL,
				VK_IMAGE_LAYOUT_GENERAL,
				VulkanConst.toVk(format),
				VulkanConst.textureUsageToVk(texture.usage(), format) | VK_USAGE_SAMPLED | VK_USAGE_TRANSFER_SRC,
				1,
				1,
				VK_QUEUE_FAMILY_IGNORED,
				false,
				VK_SHARING_MODE_EXCLUSIVE
		);
		BackendTexture backend = BackendTexture.makeVulkan(view.getWidth(0), view.getHeight(0), info);
		ColorAlphaType alpha = premultiplied ? ColorAlphaType.PREMUL : ColorAlphaType.UNPREMUL;
		Image image = Image.borrowTextureFrom(
				getContext(), backend, SurfaceOrigin.TOP_LEFT, ColorType.RGBA_8888, alpha, ColorSpace.getSRGB(), null
		);
		backend.close();
		return image;
	}

	@Override
	public void orderWriteBeforeRead(GpuTextureView view) {
		if (view.texture() instanceof VulkanGpuTexture texture && barrier != null) {
			barrier.order(texture.vkImage());
		}
	}

	@Override
	public void dispose() {
		if (barrier != null) {
			barrier.dispose();
			barrier = null;
		}
		if (cachedContext != null) {
			cachedContext.close();
			cachedContext = null;
		}
	}

}