#version 400 core

in vec2 TexCoords;
out vec4 FragColor;

uniform sampler2D texture_diffuse;

void main()
{
    vec4 texColor = texture(texture_diffuse, TexCoords);

    if(texColor.a < 0.1) {
        discard;
    }

    FragColor = texColor;
}