#version 420 compatibility
varying vec3 originalPosition;

void main()
{  
        originalPosition = vec3(gl_Vertex);
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;

}