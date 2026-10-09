#version 330
#extension GL_ARB_separate_shader_objects : require

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    mat4 TextureMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
};
layout(std140) uniform Projection {
    mat4 ProjMat;
};

layout(location = 0) in vec3 Position;
layout(location = 1) in vec4 Color;
layout(location = 2) in vec2 UV0;
layout(location = 3) in ivec2 UV1;
layout(location = 4) in ivec2 UV2;
layout(location = 5) in float LineWidth;

layout(location = 0) out vec4 vertexColor;
layout(location = 1) out vec2 v_Offset;
layout(location = 2) flat out vec2 v_HalfSize;
layout(location = 3) flat out vec4 v_Radii;
layout(location = 4) flat out float v_EdgeWidth;

void main() {
	gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
	vertexColor = Color;
	v_Offset = UV0;
	v_HalfSize = vec2(UV1) * 0.125;
	v_Radii = vec4(
		float(UV2.x & 0xFF),
		float((UV2.x >> 8) & 0xFF),
		float(UV2.y & 0xFF),
		float((UV2.y >> 8) & 0xFF)
	) * 0.25;
	v_EdgeWidth = LineWidth;
}