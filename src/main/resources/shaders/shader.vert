#version 120

attribute vec3 position;
varying vec2 vUV;

void main() {
    vUV = (position.xy * 0.5) + 0.5;
    gl_Position = gl_ModelViewProjectionMatrix * vec4(position, 1.0);
}