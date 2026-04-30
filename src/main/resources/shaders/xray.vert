#version 330 core
layout(location = 0) in vec3 aPos;
layout(location = 1) in vec2 aUV;

uniform mat4 model;
uniform mat4 viewProj;

out vec2 uv;

void main() {
    uv = aUV;
    gl_Position = viewProj * model * vec4(aPos, 1.0);
}