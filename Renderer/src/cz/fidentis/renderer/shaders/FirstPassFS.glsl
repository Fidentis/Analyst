#version 420 compatibility
varying vec3 position;
varying vec3 n;
varying vec4 shadowCoord;
varying vec3 originalPosition;
uniform vec3 sampleVertices[3];
uniform vec3 sampleNormals[3];
uniform vec3 samplePrincipalCurvature[3];
uniform vec3 sampleSecondaryCurvature[3];

layout (early_fragment_tests) in;
uniform int modelNumber;
layout (binding = 0, offset = 0) uniform atomic_uint index_counter;
layout (binding = 0)uniform sampler2D shadowMap;
layout (binding = 1, r32ui) uniform uimage2D head_pointer_image;
layout (binding = 2, rgba32ui) uniform uimageBuffer list_buffer;
layout (binding = 3) uniform sampler2D cross_texture;
 
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


vec4 shadeFragment(float shadow){       


        vec3 l = normalize(gl_LightSource[0].position.xyz - position); 

        vec4 diffuse = gl_LightSource[0].diffuse * gl_FrontMaterial.diffuse * max(0.0, dot(n, l));
        vec4 ambient = gl_LightSource[0].ambient * gl_FrontMaterial.ambient;

        vec3 c = normalize(-position); 
        vec3 s = normalize(c + l);
        vec4 specular = gl_LightSource[0].specular * gl_FrontMaterial.specular * pow(max(dot(n, s), 0.0), gl_FrontMaterial.shininess);

        vec4 color = diffuse + ambient*2 + specular/2;
        return  color;
    
}


void main(void)
{
    uint old_head;
    vec4 frag_color;

    uint index = atomicCounterIncrement(index_counter);
    old_head = imageAtomicExchange(head_pointer_image, ivec2(gl_FragCoord.xy), int(index));
    
    
        vec4 shadowCoordinateWdivide = shadowCoord / shadowCoord.w ;
        float distanceFromLight = texture2D(shadowMap,shadowCoordinateWdivide.st).z;
        shadowCoordinateWdivide.z -= 0.00002;
        float shadow = 1.0;
 	if (shadowCoord.w > 0.0){
 	shadow = distanceFromLight < shadowCoordinateWdivide.z ? 0.5 : 1.0 ;
        }

    frag_color= shadeFragment(shadow);

    uvec4 item;
    item.x = old_head;
    item.y = packUnorm4x8(frag_color);
    item.z = floatBitsToUint(gl_FragCoord.z);


    float isCross = 1.0f;
  
    for(int i=0; i<3 ; i++){
          vec3 vertex = sampleVertices[i];     
          if(distance(vertex,originalPosition)<5){
            if((vertexDistanceFromPlane(originalPosition, (vertex+1*sampleSecondaryCurvature[i]),sampleSecondaryCurvature[i])<2 && 
                vertexDistanceFromPlane(originalPosition, (vertex-1*sampleSecondaryCurvature[i]),sampleSecondaryCurvature[i])<2 && 
                vertexDistanceFromPlane(originalPosition, (vertex+3*sampleNormals[i]),sampleNormals[i])<6 &&
                vertexDistanceFromPlane(originalPosition, (vertex-3*sampleNormals[i]),sampleNormals[i])<6) ||
               (distance(vertex,originalPosition)<3 &&
                vertexDistanceFromPlane(originalPosition, (vertex+1*samplePrincipalCurvature[i]),samplePrincipalCurvature[i])<2 && 
                vertexDistanceFromPlane(originalPosition, (vertex-1*samplePrincipalCurvature[i]),samplePrincipalCurvature[i])<2 && 
                vertexDistanceFromPlane(originalPosition, (vertex+3*sampleNormals[i]),sampleNormals[i])<6 &&
                vertexDistanceFromPlane(originalPosition, (vertex-3*sampleNormals[i]),sampleNormals[i])<6))
                {
                 isCross = 0;
                 break;
                }

                
         }
    }



   

  	

    item.w = packUnorm4x8(vec4(modelNumber, isCross,shadow,0));
    memoryBarrier();
    imageStore(list_buffer, int(index), item);


      gl_FragColor = frag_color;//*texColor;

}