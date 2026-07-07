#version 330 core
in vec2 uv;
out vec4 color;

uniform sampler2D tex;
uniform float opacity;

void main() {
    vec4 texColor = texture(tex, uv);

    texColor.a *= opacity;

    if(texColor.a < 0.1) {
        discard;
    }

    color = texColor;
}
