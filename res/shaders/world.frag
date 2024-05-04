#version 130

in vec2 texcoord;

uniform sampler2D colorSampler;

uniform vec4 uv;
uniform vec4 color;

void main() {
    ivec2 colorSamplerSize = textureSize(colorSampler, 0);
    vec2 bias = 0.25 / vec2(colorSamplerSize);

    gl_FragColor = texture2D(colorSampler, texcoord * (uv.zw - bias * 2.0) + uv.xy + bias) * color;
}