#version 400 core

in vec2 TexCoords;
out vec4 FragColor;

uniform sampler2D texture_diffuse;

void main()
{
    FragColor = texture(texture_diffuse, TexCoords);
}