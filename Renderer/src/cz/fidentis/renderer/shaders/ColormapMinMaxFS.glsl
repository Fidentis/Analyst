#version 420 compatibility
uniform ivec2 startPosition;
uniform ivec2 endPosition;

// The per-pixel image containing the head pointers
layout (binding = 1, r32ui) uniform uimage2D head_pointer_image;
// Buffer containing linked lists of fragments
layout (binding = 2, rgba32ui) uniform uimageBuffer list_buffer;

// This is the maximum number of overlapping fragments allowed
#define MAX_FRAGMENTS 15

// Temporary array used for sorting fragments
uvec4 fragment_list[MAX_FRAGMENTS];
uint fragment_pointer[MAX_FRAGMENTS];
 

void main(void)
{
if(gl_FragCoord.x<1 && gl_FragCoord.y<1){
    float maxDistancel = -1.0/0.0;
    float minDistancel = 1.0/0.0;

    uint fragment_count = 0;


    ivec2 sPosition = startPosition;
    ivec2 ePosition = endPosition;
    if(endPosition.x<startPosition.x){        
      sPosition.x = endPosition.x;
      ePosition.x = startPosition.x;
    }

    if(endPosition.y<startPosition.y){
       sPosition.y = endPosition.y;
       ePosition.y = startPosition.y;
    }

//*************find minimum and maximum within selection****************************************
 int i, j;

  for( i = sPosition.x; i< ePosition.x; i++){
     for(j = sPosition.y; j<ePosition.y;j++){
         uint current_index = imageLoad(head_pointer_image, ivec2(i,j)).x;
            int c = 0;
            while (current_index > 0 && c<20)
            {
                uvec4 fragment = imageLoad(list_buffer, int(current_index));        
                current_index = fragment.x;
                    if((unpackUnorm4x8(fragment.w)).w == 1){
                         float f = uintBitsToFloat(fragment.y);
                        if(f<minDistancel){
                            minDistancel = f;
                         }
                        if(f>maxDistancel){
                          maxDistancel = f;
                        }
                }
             c++;
            }
        }
    }

    uvec4 f ;
    f.x = floatBitsToUint(minDistancel);
    f.y = floatBitsToUint(maxDistancel);
    f.z = 0;
    f.w = 0;

     memoryBarrier();
    imageStore(list_buffer, 0, f);
}
 discard;
}



