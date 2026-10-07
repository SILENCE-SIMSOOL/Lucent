package silence.simsool.lucent.skija.backend;

import java.nio.LongBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkCommandBuffer;
import org.lwjgl.vulkan.VkCommandBufferAllocateInfo;
import org.lwjgl.vulkan.VkCommandBufferBeginInfo;
import org.lwjgl.vulkan.VkCommandPoolCreateInfo;
import org.lwjgl.vulkan.VkDevice;
import org.lwjgl.vulkan.VkFenceCreateInfo;
import org.lwjgl.vulkan.VkImageMemoryBarrier;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;

import silence.simsool.lucent.Lucent;

public class VulkanFrameBarrier {

	private static final int FRAMES_IN_FLIGHT = 2;

	private final VkDevice device;
	private final VkQueue queue;
	private final int queueFamily;

	private long commandPool = 0L;
	private VkCommandBuffer[] commandBuffers = new VkCommandBuffer[0];
	private long[] fences = new long[0];
	private boolean ready = false;
	private int frame = 0;

	public VulkanFrameBarrier(VkDevice device, VkQueue queue, int queueFamily) {
		this.device = device;
		this.queue = queue;
		this.queueFamily = queueFamily;
	}

	public void order(long image) {
		if (!ready) {
			try {
				init();
			} catch (Throwable t) {
				Lucent.LOG.warn("Vulkan pipeline-barrier init failed: " + t.getMessage());
			}
			if (!ready) return;
		}

		int slot = frame % FRAMES_IN_FLIGHT;
		frame++;
		VkCommandBuffer cmd = commandBuffers[slot]; if (cmd == null) return;
		long fence = fences[slot];

		try (MemoryStack stack = MemoryStack.stackPush()) {
			LongBuffer pFence = stack.longs(fence);
			VK10.vkWaitForFences(device, pFence, true, Long.MAX_VALUE);
			VK10.vkResetFences(device, pFence);
			VK10.vkResetCommandBuffer(cmd, 0);

			VkCommandBufferBeginInfo begin = VkCommandBufferBeginInfo.calloc(stack)
					.sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_BEGIN_INFO)
					.flags(VK10.VK_COMMAND_BUFFER_USAGE_ONE_TIME_SUBMIT_BIT);
			VK10.vkBeginCommandBuffer(cmd, begin);

			VkImageMemoryBarrier.Buffer barrier = VkImageMemoryBarrier.calloc(1, stack);
			barrier.get(0).sType(VK10.VK_STRUCTURE_TYPE_IMAGE_MEMORY_BARRIER)
					.srcAccessMask(VK10.VK_ACCESS_COLOR_ATTACHMENT_WRITE_BIT | VK10.VK_ACCESS_TRANSFER_WRITE_BIT)
					.dstAccessMask(VK10.VK_ACCESS_SHADER_READ_BIT)
					.oldLayout(VK10.VK_IMAGE_LAYOUT_GENERAL)
					.newLayout(VK10.VK_IMAGE_LAYOUT_GENERAL)
					.srcQueueFamilyIndex(VK10.VK_QUEUE_FAMILY_IGNORED)
					.dstQueueFamilyIndex(VK10.VK_QUEUE_FAMILY_IGNORED)
					.image(image);
			barrier.get(0).subresourceRange()
					.aspectMask(VK10.VK_IMAGE_ASPECT_COLOR_BIT)
					.baseMipLevel(0).levelCount(1).baseArrayLayer(0).layerCount(1);

			VK10.vkCmdPipelineBarrier(
					cmd,
					VK10.VK_PIPELINE_STAGE_COLOR_ATTACHMENT_OUTPUT_BIT | VK10.VK_PIPELINE_STAGE_TRANSFER_BIT,
					VK10.VK_PIPELINE_STAGE_FRAGMENT_SHADER_BIT,
					0, null, null, barrier
			);
			VK10.vkEndCommandBuffer(cmd);

			VkSubmitInfo submit = VkSubmitInfo.calloc(stack)
					.sType(VK10.VK_STRUCTURE_TYPE_SUBMIT_INFO)
					.pCommandBuffers(stack.pointers(cmd));
			VK10.vkQueueSubmit(queue, submit, fence);
		}
	}

	private void init() {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			VkCommandPoolCreateInfo poolInfo = VkCommandPoolCreateInfo.calloc(stack)
					.sType(VK10.VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
					.flags(VK10.VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT)
					.queueFamilyIndex(queueFamily);
			LongBuffer pPool = stack.mallocLong(1);
			if (VK10.vkCreateCommandPool(device, poolInfo, null, pPool) != VK10.VK_SUCCESS) {
				throw new IllegalStateException("vkCreateCommandPool failed");
			}
			commandPool = pPool.get(0);

			VkCommandBufferAllocateInfo allocInfo = VkCommandBufferAllocateInfo.calloc(stack)
					.sType(VK10.VK_STRUCTURE_TYPE_COMMAND_BUFFER_ALLOCATE_INFO)
					.commandPool(commandPool)
					.level(VK10.VK_COMMAND_BUFFER_LEVEL_PRIMARY)
					.commandBufferCount(FRAMES_IN_FLIGHT);
			PointerBuffer pBuffers = stack.mallocPointer(FRAMES_IN_FLIGHT);
			if (VK10.vkAllocateCommandBuffers(device, allocInfo, pBuffers) != VK10.VK_SUCCESS) {
				throw new IllegalStateException("vkAllocateCommandBuffers failed");
			}
			commandBuffers = new VkCommandBuffer[FRAMES_IN_FLIGHT];
			for (int i = 0; i < FRAMES_IN_FLIGHT; i++) {
				commandBuffers[i] = new VkCommandBuffer(pBuffers.get(i), device);
			}

			VkFenceCreateInfo fenceInfo = VkFenceCreateInfo.calloc(stack)
					.sType(VK10.VK_STRUCTURE_TYPE_FENCE_CREATE_INFO)
					.flags(VK10.VK_FENCE_CREATE_SIGNALED_BIT);
			fences = new long[FRAMES_IN_FLIGHT];
			LongBuffer pFence = stack.mallocLong(1);
			for (int i = 0; i < FRAMES_IN_FLIGHT; i++) {
				if (VK10.vkCreateFence(device, fenceInfo, null, pFence) != VK10.VK_SUCCESS) {
					throw new IllegalStateException("vkCreateFence failed");
				}
				fences[i] = pFence.get(0);
			}
		}
		ready = true;
	}

	public void dispose() {
		if (ready) {
			try {
				VK10.vkDeviceWaitIdle(device);
				for (long f : fences) {
					VK10.vkDestroyFence(device, f, null);
				}
				if (commandPool != 0L) {
					VK10.vkDestroyCommandPool(device, commandPool, null);
				}
			} catch (Throwable t) {
				Lucent.LOG.warn("Vulkan barrier teardown failed: " + t.getMessage());
			}
		}
		ready = false;
		commandPool = 0L;
		commandBuffers = new VkCommandBuffer[0];
		fences = new long[0];
		frame = 0;
	}

}