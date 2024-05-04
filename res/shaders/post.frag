#version 130

in vec2 texcoord;

uniform sampler2D colorSampler;
uniform vec3 color;

void main() {
    gl_FragColor = vec4(texture2D(colorSampler, texcoord).rgb * color, 1.0);
}