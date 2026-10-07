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

layout(location = 0) out vec4 f_Color;
layout(location = 1) out vec2 f_Position;

void main() {
	f_Color = Color;
	f_Position = Position.xy;
	gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);
}