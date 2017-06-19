#version 420 compatibility
varying vec3 position;
varying vec3 originalPosition;
varying vec3 n;
varying float currentDistance;
uniform int selectionType;
uniform vec3 selectionCameraPosition;
uniform vec3 selectionRectangle[4];


layout (binding = 0, offset = 0) uniform atomic_uint index_counter;
layout (binding = 1, r32ui) uniform uimage2D head_pointer_image;
layout (binding = 2, rgba32ui) uniform uimageBuffer list_buffer;
layout(origin_upper_left) in vec4 gl_FragCoord;

 /**
     *
     * @param pl line point
     * @param u line vector
     * @param n plane normal
     * @param p point from plane
     * @return intersection of line and plane, if it doesn't exist returns null
     */
    vec3 findLinePlaneIntersection(vec3 pl, vec3 u, vec3 n, vec3 p) {
        vec3 ret;
        vec3 w = pl-p;  

        float D = dot(n,u);
        float N = -dot(n,w);

        if (abs(D) == 0) {   // segment is parallel to plane
            if (N == 0) // segment lies in plane
            {
                return  pl;
            } else {
                return vec3(0,0,0);                    // no intersection
            }
        }else{
            // they are not parallel
            // compute intersect param
            float sI = N / D;


            ret = u*sI + pl;


            return ret;
     }
    }


vec4 shadeFragment(vec4 color){   

        vec3 l = normalize(gl_LightSource[0].position.xyz - position); 

        vec4 diffuse = gl_LightSource[0].diffuse * gl_FrontMaterial.diffuse * max(0.0, dot(n, l));
        vec4 ambient = gl_LightSource[0].ambient * gl_FrontMaterial.ambient;

        vec3 c = normalize(-position); 
        vec3 s = normalize(c + l);
        vec4 specular = gl_LightSource[0].specular * gl_FrontMaterial.specular * pow(max(dot(n, s), 0.0), gl_FrontMaterial.shininess);

        vec4 cl = color*(diffuse + ambient*5 + specular/5);
        return  cl;
    
}


void main(void)
{
    
    uint old_head;
    vec4 frag_color;

    uint index = atomicCounterIncrement(index_counter)+1;
    old_head = imageAtomicExchange(head_pointer_image, ivec2(gl_FragCoord.xy), int(index));
    

    vec4 color = shadeFragment(vec4 (0.9f,0.9f,0.9f,1));       
  
   
    uvec4 item;
    item.x = old_head;
    item.y = floatBitsToUint(currentDistance);	
    item.z = floatBitsToUint(gl_FragCoord.z); 

   
    float selected = 0;
    vec3 p = (originalPosition - selectionCameraPosition);
    if(selectionType==0){
        vec3 normal = normalize(cross((selectionRectangle[1]-selectionRectangle[0]),(selectionRectangle[3]-selectionRectangle[0])));
        vec3 proj = findLinePlaneIntersection(originalPosition, p, normal, selectionRectangle[0]);

        float a = (selectionRectangle[0].x -  selectionRectangle[2].x)/2.0f;
        float b = (selectionRectangle[0].y -  selectionRectangle[2].y)/2.0f;
        vec3 center = (selectionRectangle[0] +  selectionRectangle[2])/2.0f;

        float ec = (proj.x - center.x)*(proj.x - center.x)/(a*a) + (proj.y - center.y)*(proj.y - center.y)/(b*b);


        if(ec <= 1){
              selected =1;         
        }
    }
    else if(selectionType==1){
        vec3 n1  = cross((selectionRectangle[0]-selectionCameraPosition),(selectionRectangle[3]-selectionCameraPosition) );
        vec3 n2  = cross((selectionRectangle[1]-selectionCameraPosition),(selectionRectangle[2]-selectionCameraPosition) );
        vec3 n3  = cross((selectionRectangle[0]-selectionCameraPosition),(selectionRectangle[1]-selectionCameraPosition) );
        vec3 n4  = cross((selectionRectangle[3]-selectionCameraPosition),(selectionRectangle[2]-selectionCameraPosition) );

        if((selectionRectangle[0]==selectionRectangle[1])||(sign(dot(p,n1))!=sign(dot(p,n2))&&sign(dot(p,n3))!=sign(dot(p,n4)))){        
                selected =1;           
        }

    }
    else if(selectionType==-1){
        selected =1;  
    }
     item.w = packUnorm4x8(vec4(color.x,color.y,color.z,selected));

    memoryBarrier();
    imageStore(list_buffer, int(index), item);
    
    gl_FragColor = color;
}



