#version 420 compatibility
varying vec3 position;
varying vec3 originalPosition;
varying vec3 n;
varying vec4 shadowCoord;

#define M_PI 3.1415926535897932384626433832795

void main()
{
        originalPosition = vec3(gl_Vertex);
        position = vec3(gl_ModelViewMatrix * gl_Vertex); 
        n = normalize(gl_NormalMatrix * gl_Normal);
        shadowCoord = gl_TextureMatrix[7] * gl_Vertex;

	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
  
}