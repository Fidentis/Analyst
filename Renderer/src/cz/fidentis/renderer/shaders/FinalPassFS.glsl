#version 420 compatibility
layout (binding = 1, r32ui) uniform uimage2D head_pointer_image;
layout (binding = 2, rgba32ui) uniform uimageBuffer list_buffer;

layout (location = 0) out vec4 color;

uvec4 fragment_list[2];


void main(void)
{
    vec4 final_color =  vec4(vec3(1),0);
    uint current_index;
    uint fragment_count = 0;

    current_index = imageLoad(head_pointer_image, ivec2(gl_FragCoord).xy).x;

    while (current_index != 0 && fragment_count < 2)// we only need first 2 fragments
    {
        uvec4 fragment = imageLoad(list_buffer, int(current_index));
        fragment_list[fragment_count] = fragment;
        current_index = fragment.x;
        fragment_count++;
    }
    int crossing = 0;

   
    ivec2 coords = ivec2(gl_FragCoord).xy;
    if(fragment_count> 1){
        uvec4 fragment =fragment_list[0];
        uvec4 nextFragment =fragment_list[1];
        uint model = uint(unpackUnorm4x8(fragment.w).x);
        for (int i = -1; i<2;i++){
            for (int j = -1; j<2;j++){
                uvec4 temp_fragment_list[2];
                uint index = imageLoad(head_pointer_image, ivec2(coords.x+i, coords.y+j)).x;
                uint temp_fragment_count = 0;
                while (index != 0 && temp_fragment_count < 2 )// we only need first 2 fragments
                {
                        uvec4 neighbour  = imageLoad(list_buffer, int(index));
                        temp_fragment_list[temp_fragment_count] = neighbour;
                        index = neighbour.x;
                        temp_fragment_count++;
                }

                if(temp_fragment_count>1){
                uvec4 neighbour = temp_fragment_list[0];
                uint neighbourModel = uint(unpackUnorm4x8(neighbour.w).x);
                    if(neighbourModel != model){                         
                        uvec4 nextNeighbour =  temp_fragment_list[1];
                        //  uint(unpackUnorm4x8(nextFragment.w).x)==neighbourModel && uint(unpackUnorm4x8(nextNeighbour.w).x)==model
                        if(abs(uintBitsToFloat(fragment.z)-uintBitsToFloat(neighbour.z))<0.002f){                 
                            crossing ++;
                        }
                    }
                }                 
            }
       }
        
    }
     
    if(crossing == 0 ){             
        discard;
    }
    else{
        final_color = vec4(0,0,0,1);
    }
    color = final_color;
}