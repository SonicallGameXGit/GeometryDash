#version 130

in vec2 position;

uniform mat4 project;
uniform mat4 view;
uniform mat4 transform;

out vec2 texcoord;

void main() {
    gl_Position = project * view * transform * vec4(position, 0.0, 1.0);
    texcoord = position + 0.5;
}