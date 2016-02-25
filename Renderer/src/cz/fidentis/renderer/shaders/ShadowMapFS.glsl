#version 420 compatibility
varying vec3 originalPosition;
uniform vec3 sampleVertices[3];
uniform vec3 sampleNormals[3];
uniform vec3 samplePrincipalCurvature[3];
uniform vec3 sampleSecondaryCurvature[3];


float vertexDistanceFromPlane(vec3 vertex, vec3 planePoint, vec3 planeNormal){
        vec3 w = vec3(vertex-planePoint);

        float D = dot(planeNormal,planeNormal);
        float N = -dot(w,planeNormal);

        // they are not parallel
        // compute intersect param
        float sI = N / D;

        vec3 intersection = vec3(planeNormal*sI + vertex);

        return distance(intersection,vertex);

}

void main(){	
     float isCross = 1.0f;
     for(int i=0; i<3 ; i++){
          vec3 vertex = sampleVertices[i];     
          if(distance(vertex,originalPosition)<5.0f){
            if((vertexDistanceFromPlane(originalPosition, (vertex+1*sampleSecondaryCurvature[i]),sampleSecondaryCurvature[i])<2.0f && 
                vertexDistanceFromPlane(originalPosition, (vertex-1*sampleSecondaryCurvature[i]),sampleSecondaryCurvature[i])<2.0f && 
                vertexDistanceFromPlane(originalPosition, (vertex+3*sampleNormals[i]),sampleNormals[i])<6.0f &&
                vertexDistanceFromPlane(originalPosition, (vertex-3*sampleNormals[i]),sampleNormals[i])<6.0f) ||
               (distance(vertex,originalPosition)<2.5f &&
                vertexDistanceFromPlane(originalPosition, (vertex+1*samplePrincipalCurvature[i]),samplePrincipalCurvature[i])<2.0f && 
                vertexDistanceFromPlane(originalPosition, (vertex-1*samplePrincipalCurvature[i]),samplePrincipalCurvature[i])<2.0f && 
                vertexDistanceFromPlane(originalPosition, (vertex+3*sampleNormals[i]),sampleNormals[i])<6.0f &&
                vertexDistanceFromPlane(originalPosition, (vertex-3*sampleNormals[i]),sampleNormals[i])<6.0f))
                {
                 isCross = 0.0f;
                 break;
                }
             }
                
         }

    if(isCross == 1.0f){
        discard;
    }

    gl_FragColor = vec4(0.0f);
  
}