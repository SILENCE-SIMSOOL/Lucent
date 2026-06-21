package silence.simsool.lucent.general.utils.render;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import java.lang.reflect.Method;

public class IrisCompatImpl implements IrisCompatibility {

	private static Object irisApiInstance;
	private static Method assignPipelineMethod;
	private static Object basicProgram;
	private static Object linesProgram;
	private static boolean initialized = false;

	static {
		try {
			Class<?> irisApiClass = Class.forName("net.irisshaders.iris.api.v0.IrisApi");
			Method getInstanceMethod = irisApiClass.getMethod("getInstance");
			irisApiInstance = getInstanceMethod.invoke(null);

			Class<?> irisProgramClass = Class.forName("net.irisshaders.iris.api.v0.IrisProgram");
			for (Object enumConstant : irisProgramClass.getEnumConstants()) {
				Enum<?> e = (Enum<?>) enumConstant;
				if ("BASIC".equals(e.name())) {
					basicProgram = enumConstant;
				} else if ("LINES".equals(e.name())) {
					linesProgram = enumConstant;
				}
			}

			assignPipelineMethod = irisApiClass.getMethod("assignPipeline", RenderPipeline.class, irisProgramClass);
			initialized = true;
		} catch (Exception e) {
			// Failed to initialize Iris API reflection
		}
	}

	@Override
	public void registerPipeline(RenderPipeline pipeline, IrisShaderType shaderType) {
		if (!initialized) return;
		try {
			Object programType = (shaderType == IrisShaderType.BASIC) ? basicProgram : linesProgram;
			if (programType != null) {
				assignPipelineMethod.invoke(irisApiInstance, pipeline, programType);
			}
		} catch (Exception e) {
			// Failed to assign pipeline
		}
	}
}
