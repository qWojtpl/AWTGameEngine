#version 120
uniform sampler2D sceneTex;
uniform vec2 resolution;

void main() {
    vec2 uv = gl_FragCoord.xy / resolution;
    vec3 col = texture2D(sceneTex, uv).rgb;
    float shadow = smoothstep(0.0, 0.25, uv.y);
    col = col * shadow;
    gl_FragColor = vec4(col, 1.0);
}