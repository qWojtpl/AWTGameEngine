#version 330 core
layout(location = 0) in vec3 aPos;
layout(location = 1) in vec3 normal;
layout(location = 2) in vec2 aUV;

out vec2 uv;

void main() {
    uv = aUV;

    mat4 identity = mat4(1.0);
    gl_Position = identity * identity * vec4(aPos, 1.0);
}
