#version 130

in vec2 position;
out vec2 texcoord;

void main() {
    gl_Position = vec4(position * 2.0, 0.0, 1.0);
    texcoord = position + 0.5;
}