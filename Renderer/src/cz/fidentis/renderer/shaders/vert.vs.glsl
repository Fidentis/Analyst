#version 330

in vec3 position;
in vec3 normal;
in vec4 color;

out vec4 vertColor;
out vec3 vertNormal;
out vec3 light;

uniform mat4 MVP;
uniform mat3 N;

void main() {
    vertColor = color;
    
    vertNormal = N * normal;

    light = vec3(0,0,-30);

    gl_Position = MVP * vec4(position, 1.0);
}

