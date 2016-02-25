#version 420 compatibility
varying vec3 originalPosition;
varying vec3 position;
varying vec3 n;
in float cDistance;
varying float currentDistance;

void main()
{
       originalPosition = vec3(gl_Vertex);
       position = vec3(gl_ModelViewMatrix * gl_Vertex); 
       n = normalize(gl_NormalMatrix * gl_Normal);
       currentDistance = cDistance;
       gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}