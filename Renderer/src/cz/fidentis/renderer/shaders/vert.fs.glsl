#version 330

out vec4 fragColor;

in vec4 vertColor;
in vec3 vertNormal;
in vec3 light;

void main() {
    vec3 n = normalize(vertNormal);

    float d = dot(n, normalize(light));
    fragColor = vec4(vertColor);
}