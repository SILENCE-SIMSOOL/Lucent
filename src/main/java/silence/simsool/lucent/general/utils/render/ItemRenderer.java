package silence.simsool.lucent.general.utils.render;

import static silence.simsool.lucent.Lucent.mc;

import java.util.Objects;

import org.joml.Matrix3x2f;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.item.TrackingItemStackRenderState;
import net.minecraft.client.renderer.state.gui.BlitRenderState;
import net.minecraft.client.renderer.state.gui.GuiItemRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.PictureInPictureRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.LightCoordsUtil;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class ItemRenderer extends PictureInPictureRenderer<ItemRenderer.State> {
	private GpuTextureView textureView;
	private State lastState;

	public ItemRenderer() {
		super();
	}

	@Override
	protected void renderToTexture(State state, PoseStack poseStack, net.minecraft.client.renderer.SubmitNodeCollector submitNodeCollector) {
		this.textureView = RenderSystem.outputColorTextureOverride;
		this.lastState = state;
		poseStack.scale(1f, -1f, -1f);

		if (state.state.itemStackRenderState().usesBlockLight()) mc.gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_3D);
		else mc.gameRenderer.lighting().setupFor(Lighting.Entry.ITEMS_FLAT);

		state.state.itemStackRenderState().submit(poseStack, submitNodeCollector, LightCoordsUtil.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, 0);
	}

	@Override
	protected void blitTexture(State element, GuiRenderState state) {
		state.addBlitToCurrentLayer(
				new BlitRenderState(
						RenderPipelines.GUI_TEXTURED_PREMULTIPLIED_ALPHA,
						TextureSetup.singleTexture(textureView, RenderSystem.getSamplerCache().getRepeat(FilterMode.LINEAR)),
						element.pose(), element.x0(), element.y0(), element.x0() + 16, element.y0() + 16, 0.0f, 1.0f, 1.0f,
						0.0f, -1, element.scissorArea(), null
				)
		);
	}

	@Override
	protected boolean textureIsReadyToBlit(State state) {
		return lastState != null && lastState.equals(state);
	}

	@Override
	protected float getTranslateY(int height, int windowScaleFactor) {
		return height / 2f;
	}

	@Override
	public Class<State> getRenderStateClass() {
		return State.class;
	}

	@Override
	protected String getTextureLabel() {
		return "item_state";
	}

	public static class State implements PictureInPictureRenderState {
		private final GuiItemRenderState state;

		public State(GuiItemRenderState state) {
			this.state = state;
		}

		@Override
		public float scale() {
			return Math.max(state.pose().m00(), state.pose().m11()) * 16f;
		}

		@Override
		public int x0() {
			return state.x();
		}

		@Override
		public int y0() {
			return state.y();
		}

		@Override
		public int x1() {
			return state.x() + (int) scale();
		}

		@Override
		public int y1() {
			return state.y() + (int) scale();
		}

		@Override
		public ScreenRectangle scissorArea() {
			return state.scissorArea();
		}

		@Override
		public ScreenRectangle bounds() {
			return state.bounds();
		}

		@Override
		public Matrix3x2f pose() {
			return state.pose();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			State other = (State) o;
			return (
					Objects.equals(
						state.itemStackRenderState().getModelIdentity(), other.state.itemStackRenderState().getModelIdentity()
					)
					&& state.pose().m00() == other.state.pose().m00() && state.pose().m11() == other.state.pose().m11()
			);
		}

		@Override
		public int hashCode() {
			return Objects.hash(state.itemStackRenderState().getModelIdentity(), state.pose().m00(), state.pose().m11());
		}
	}

	public static void drawItemStack(GuiGraphicsExtractor graphics, ItemStack item, int x, int y) {
		if (item.isEmpty()) return;

		TrackingItemStackRenderState tracking = new TrackingItemStackRenderState();
		mc.getItemModelResolver().updateForTopItem(tracking, item, ItemDisplayContext.GUI, mc.level, mc.player, 0);

		
		State state = new State(new GuiItemRenderState(new Matrix3x2f(graphics.pose()), tracking, x, y, graphics.scissorStack.peek()));
		graphics.guiRenderState.addPicturesInPictureState(state);
	}
}