#version 130

in vec2 texcoord;

uniform sampler2D colorSampler;
uniform vec4 uv;

void main() {
    gl_FragColor = texture2D(colorSampler, texcoord * uv.zw + uv.xy);
}