#version 400 core

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 normal;
layout (location = 2) in vec2 aTexCoords;

out vec2 TexCoords;

uniform mat4 model;
uniform mat4 viewProj;

void main()
{
    TexCoords = aTexCoords;

    vec3 worldPosition = vec3(model[3][0], model[3][1], model[3][2]);

    float scaleX = length(vec3(model[0][0], model[0][1], model[0][2]));
    float scaleY = length(vec3(model[1][0], model[1][1], model[1][2]));

    vec3 cameraRight = vec3(viewProj[0][0], viewProj[1][0], viewProj[2][0]);
    vec3 cameraUp    = vec3(viewProj[0][1], viewProj[1][1], viewProj[2][1]);

    cameraRight = normalize(cameraRight);
    cameraUp = normalize(cameraUp);

    vec3 billboardWorldPos = worldPosition
    + cameraRight * aPos.x * scaleX
    + cameraUp    * aPos.y * scaleY;

    gl_Position = viewProj * vec4(billboardWorldPos, 1.0);
}