package silence.simsool.lucent.general.utils.render;

import java.util.function.Consumer;

import org.joml.Matrix3x2f;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;

public class LucentGuiRenderState implements GuiElementRenderState {
	private final RenderPipeline pipeline;
	private final TextureSetup textureSetup;
	private final ScreenRectangle scissorArea;
	private final ScreenRectangle bounds;
	private final Consumer<VertexConsumer> vertexConsumerFunc;

	public LucentGuiRenderState(RenderPipeline pipeline, TextureSetup textureSetup, ScreenRectangle scissorArea, ScreenRectangle bounds, Consumer<VertexConsumer> vertexConsumerFunc) {
		this.pipeline = pipeline;
		this.textureSetup = textureSetup;
		this.scissorArea = scissorArea;
		this.bounds = bounds;
		this.vertexConsumerFunc = vertexConsumerFunc;
	}

	public LucentGuiRenderState(RenderPipeline pipeline, TextureSetup textureSetup, GuiGraphics context, ScreenRectangle bounds, Consumer<VertexConsumer> vertexConsumerFunc) {
		this(pipeline, textureSetup, context.scissorStack.peek(), bounds, vertexConsumerFunc);
	}

	@Override
	public void buildVertices(VertexConsumer vertices) {
		vertexConsumerFunc.accept(vertices);
	}

	@Override
	public RenderPipeline pipeline() {
		return pipeline;
	}

	@Override
	public TextureSetup textureSetup() {
		return textureSetup;
	}

	@Override
	public ScreenRectangle scissorArea() {
		return scissorArea;
	}

	@Override
	public ScreenRectangle bounds() {
		return bounds;
	}

	public static ScreenRectangle createBounds(int x0, int y0, int x1, int y1, Matrix3x2f pose, ScreenRectangle scissorArea) {
		ScreenRectangle screenRect = new ScreenRectangle(x0, y0, x1 - x0, y1 - y0).transformMaxBounds(pose);
		return scissorArea != null ? scissorArea.intersection(screenRect) : screenRect;
	}
}