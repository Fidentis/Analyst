#version 420 compatibility
uniform vec4 fogColor;
uniform int fogVersion;
uniform float minZ;
uniform float maxZ;
uniform bool innerSurfaceSolid; 
// The per-pixel image containing the head pointers
layout (binding = 1, r32ui) uniform uimage2D head_pointer_image;
// Buffer containing linked lists of fragments
layout (binding = 2, rgba32ui) uniform uimageBuffer list_buffer;

// This is the output color
layout (location = 0) out vec4 color;

// This is the maximum number of overlapping fragments allowed
#define MAX_FRAGMENTS 15

#define M_PI 3.1415926535897932384626433832795

// Temporary array used for sorting fragments
uvec4 fragment_list[MAX_FRAGMENTS];
uint fragment_pointer[MAX_FRAGMENTS];



vec3 rgb2hsv(vec3 color)
{
    vec3 hsv  ;
    float      minC, maxC, delta;

    minC = color.r < color.g ? color.r : color.g;
    minC = minC  < color.b ? minC  : color.b;

    maxC = color.r > color.g ? color.r : color.g;
    maxC = maxC  > color.b ? maxC  : color.b;

    hsv.b = maxC;                         
    delta = maxC - minC;

    if(maxC > 0) { 
        hsv.g = (delta / maxC);               
    } else {
        hsv.g = 0.0f;
        hsv.r = -1;                            // its now undefined
        return hsv;
    }
    if( color.r >= maxC )   {                        
        hsv.r = ( color.g - color.b ) / delta; }       // between yellow & magenta
    else if( color.g >= maxC ){
        hsv.r = 2.0f + ( color.b - color.r ) / delta; } // between cyan & yellow
    else{
        hsv.r = 4.0f + ( color.r - color.g ) / delta;}  // between magenta & cyan

    hsv.r *= 60.0f;                            

    if( hsv.r < 0.0f )
        hsv.r += 360.0f;

    return hsv;
}


vec3 hsv2rgb(vec3 color)
{
    float hh, p, q, t, ff;
    int  i;
    vec3  rgb;

    if(color.g <= 0.0) {      
        rgb.r = color.b;
        rgb.g = color.b;
        rgb.b = color.b;
        return rgb;
    }
    hh = color.r;
    if(hh >= 360.0) hh = 0.0;
    hh /= 60.0;
    i = int(hh);
    ff = hh - i;
    p = color.b * (1.0 - color.g);
    q = color.b * (1.0 - (color.g * ff));
    t = color.b * (1.0 - (color.g * (1.0 - ff)));

    switch(i) {
    case 0:
        rgb.r = color.b;
        rgb.g = t;
        rgb.b = p;
        break;
    case 1:
        rgb.r = q;
        rgb.g = color.b;
        rgb.b = p;
        break;
    case 2:
        rgb.r = p;
        rgb.g = color.b;
        rgb.b = t;
        break;

    case 3:
        rgb.r = p;
        rgb.g = q;
        rgb.b = color.b;
        break;
    case 4:
        rgb.r = t;
        rgb.g = p;
        rgb.b = color.b;
        break;
    case 5:
    default:
        rgb.r = color.b;
        rgb.g = p;
        rgb.b = q;
        break;
    }
    return rgb;     
}




void main(void)
{
    uint current_index;
    uint fragment_count = 0;

    current_index = imageLoad(head_pointer_image, ivec2(gl_FragCoord).xy).x;


    while (current_index != 0 && fragment_count < MAX_FRAGMENTS)
    {
        uvec4 fragment = imageLoad(list_buffer, int(current_index));
        fragment_list[fragment_count] = fragment;
        fragment_pointer[fragment_count] = current_index;
        current_index = fragment.x;
        fragment_count++;
    }

    uint i, j;
/*************************sorting**************************************************/
    if (fragment_count > 1)
    {

        for (i = 0; i < fragment_count - 1; i++)
        {
            for (j = i + 1; j < fragment_count; j++)
            {
                uvec4 fragment1 = fragment_list[i];
                uvec4 fragment2 = fragment_list[j];

                float depth1 = uintBitsToFloat(fragment1.z);
                float depth2 = uintBitsToFloat(fragment2.z);
                                            

                if (depth1 < depth2)
                {
                    fragment_list[i] = fragment2;
                    fragment_list[j] = fragment1;

                    uint temp = fragment_pointer[i];
                    fragment_pointer[i] = fragment_pointer[j];
                    fragment_pointer[j] = temp;

                }
             
            }
        }

    }
 /*************************transparency  & color modifications**************************************************/
    
        //start with nearest fragment, 
        //while fragments belong to same object, render transparent, if objcts change, check for in/out - defending on number of same fragments;
          
         if(fragment_count==1){
            vec4 modulator = unpackUnorm4x8(fragment_list[fragment_count-1].y);  
            if(uint(unpackUnorm4x8(fragment_list[fragment_count-1].w).y)==0){                             
                             modulator.a = 1;                            
             }
            modulator.a = modulator.a+0.1f;
            fragment_list[fragment_count-1].y = packUnorm4x8(modulator); 
         }
        if(fragment_count>1){
            vec4 modulator = unpackUnorm4x8(fragment_list[fragment_count-1].y);  
            uint lastModel = uint(unpackUnorm4x8(fragment_list[fragment_count-1].w).x);
            uint sameFragCount = 1;
            bool visible = true;

                    
              //fogging            
            if(fogVersion == 1){
                // 1 overlay fog 
                modulator = mix(modulator,fogColor,5*abs(uintBitsToFloat(fragment_list[fragment_count-1].z)-uintBitsToFloat(fragment_list[fragment_count-2].z))/abs(minZ-maxZ)); 
            }
            // 2 colorize, modify alfa of outer layer
            else if(fogVersion == 2){
                   modulator.a = 0.2f*modulator.a+8*abs(uintBitsToFloat(fragment_list[fragment_count-1].z)-uintBitsToFloat(fragment_list[fragment_count-2].z))/abs(minZ-maxZ);
                   vec3 temp =  rgb2hsv(modulator.rgb);
                   temp.r = rgb2hsv(fogColor.rgb).r;
                   temp.g = rgb2hsv(fogColor.rgb).g;
                   modulator = vec4(hsv2rgb(temp),modulator.a );
                 
            }
            //ak je na danom mieste glyf, znepriehladni
            if(uint(unpackUnorm4x8(fragment_list[fragment_count-1].w).y)==0){                             
                             modulator.a = 1;                            
                             visible=false;
            }

             fragment_list[fragment_count-1].y = packUnorm4x8(modulator); 
             

             for (i = 0; i < fragment_count-1; i++) { 
                j = fragment_count-2 - i;
                //pokial je fragment zakryty, zpriehladni ho 
                if(!visible && innerSurfaceSolid){
                    vec4 modulator = unpackUnorm4x8(fragment_list[j].y);
                        modulator.a = 0;
                        fragment_list[j].y = packUnorm4x8(modulator); 
                }else{
                    //ak sa jedna o povrch rovnakeho modelu ako u predchadzajuceho fragmentu
                    if(uint(unpackUnorm4x8(fragment_list[j].w).x) == lastModel){
                       //ak obsahuje glyf, znepriehladni  
                        vec4 modulator = unpackUnorm4x8(fragment_list[j].y);
                        if(uint(unpackUnorm4x8(fragment_list[j].w).y)==0){                                
                             modulator.a = 1;                             
                             visible=false;
                        } 
                        else{
                             modulator.a = modulator.a;
                        }
                        //fogging
                        //ak je pocet predchodzich rovnakych povrchov neparny - teda aktualny fragment je vnutorny 
                        if(fogVersion == 3 && mod(sameFragCount,2)==1){
                            //3 map fog on inner surface
                            modulator = mix(modulator,fogColor,9*abs(uintBitsToFloat(fragment_list[j].z)-uintBitsToFloat(fragment_list[j+1].z))/abs(minZ-maxZ)); 
                                     
                        }
                        fragment_list[j].y = packUnorm4x8(modulator);
                        sameFragCount ++;                
                    }else{
                        lastModel = uint(unpackUnorm4x8(fragment_list[j].w).x);
                        //ak je pocet predchodzich rovnakych povrchov neparny - teda aktualny fragment je vnutorny 
                        if(mod(sameFragCount,2)==1){                                                     
                               if(innerSurfaceSolid){
                                     vec4 modulator = unpackUnorm4x8(fragment_list[j].y);
                                     modulator = vec4(modulator.rgb*unpackUnorm4x8(fragment_list[j].w).z, 1);
                                    if(fogVersion == 3){ 
                                    //3 map fog on inner surface
                                          modulator = mix(modulator,fogColor,9*abs(uintBitsToFloat(fragment_list[j].z)-uintBitsToFloat(fragment_list[j+1].z))/abs(minZ-maxZ)); 
                                    } 
                                    fragment_list[j].y = packUnorm4x8(modulator);  
                                }                                                                   
                            visible=false;
                        }
                        sameFragCount = 1;
                    } 
                } 
            } 
        }


 /*************************final color calculation  & list revrsion**************************************************/
        if(fragment_count>0){
                imageAtomicExchange(head_pointer_image, ivec2(gl_FragCoord.xy), fragment_pointer[fragment_count-1]);
        }

        vec4 final_color = vec4(vec3(1),0);
        for (i = 0; i < fragment_count; i++)
        {
            //store the list in reverse order - so the nearest fragment is the first
            if(i<fragment_count-1){
                fragment_list[fragment_count-i-1].x = fragment_pointer[fragment_count-i-2];
            }else{
                fragment_list[0].x = 0;
            }
            imageStore(list_buffer, int(fragment_pointer[fragment_count-i-1]), fragment_list[fragment_count-i-1]);

            vec4 modulator = unpackUnorm4x8(fragment_list[i].y);            
            final_color = mix(final_color, modulator, modulator.a);
        }
        
     //   if(fragment_count>1){
     //      final_color =mix(final_color,vec4(0,0,1,1),5*abs(uintBitsToFloat(fragment_list[fragment_count-1].z)-uintBitsToFloat(fragment_list[fragment_count-2].z))/abs(minZ-maxZ)); 
     //   }

        color = final_color;
  
}