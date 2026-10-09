#version 330
#extension GL_ARB_separate_shader_objects : require

layout(std140) uniform DynamicTransforms {
    mat4 ModelViewMat;
    mat4 TextureMat;
    vec4 ColorModulator;
    vec3 ModelOffset;
};

layout(location = 0) in vec4 vertexColor;
layout(location = 1) in vec2 v_Offset;
layout(location = 2) flat in vec2 v_HalfSize;
layout(location = 3) flat in vec4 v_Radii;
layout(location = 4) flat in float v_EdgeWidth;

layout(location = 0) out vec4 fragColor;

float cornerRadius(vec2 p, vec4 r) {
	float sx = step(0.0, p.x);
	float sy = step(0.0, p.y);
	float top = mix(r.x, r.y, sx);
	float bottom = mix(r.w, r.z, sx);
	return mix(top, bottom, sy);
}

float roundedRectSDF(vec2 p, vec2 halfSize, float r) {
	vec2 q = abs(p) - halfSize + r;
	return length(max(q, 0.0)) + min(max(q.x, q.y), 0.0) - r;
}

void main() {
	float r = clamp(cornerRadius(v_Offset, v_Radii), 0.0, min(v_HalfSize.x, v_HalfSize.y));
	float d = roundedRectSDF(v_Offset, v_HalfSize, r);
	float blur = max(v_EdgeWidth, 0.001);
	float alpha = clamp(0.5 - d / blur, 0.0, 1.0);

	if (alpha <= 0.0) {
		discard;
	}

	fragColor = vec4(vertexColor.rgb, vertexColor.a * alpha) * ColorModulator;
}