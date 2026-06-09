package silence.simsool.lucent.general.utils.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.rendertype.RenderType;

public interface IrisCompatibility {

	IrisCompatibility INSTANCE = resolve();

	void registerPipeline(RenderPipeline pipeline, IrisShaderType shaderType);

	default void registerRenderType(RenderType renderType, IrisShaderType shaderType) {
		registerPipeline(renderType.pipeline(), shaderType);
	}

	static void init() {
		INSTANCE.registerRenderType(LucentRenderType.LINES_ESP, IrisShaderType.LINES);
		INSTANCE.registerRenderType(LucentRenderType.LINES_TRANSLUCENT_ESP, IrisShaderType.LINES);
		INSTANCE.registerRenderType(LucentRenderType.QUADS_ESP, IrisShaderType.BASIC);
	}

	private static IrisCompatibility resolve() {
		if (FabricLoader.getInstance().isModLoaded("iris")) {
			try {
				return (IrisCompatibility) Class.forName("silence.simsool.lucent.general.utils.render.IrisCompatImpl")
						.getDeclaredConstructor().newInstance();
			} catch (Exception e) {
				// Fallback to NoOp
			}
		}
		return new IrisCompatNoOp();
	}
}
