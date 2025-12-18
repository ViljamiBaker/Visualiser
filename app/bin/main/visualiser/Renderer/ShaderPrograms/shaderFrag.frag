#version 330 core
out vec4 FragColor;

in vec3 FragPos;
in vec3 Center;
in vec3 Color;

uniform float sr;
uniform float falloffRate;
uniform float falloffDist;
uniform float minFalloff;
//in vec3 Normal;
//in vec2 TexCoords;

void main(){
    vec3 diff = (FragPos-Center);
    if(diff.x*diff.x+(diff.y/sr)*(diff.y/sr)<0.01){
        if(minFalloff==1){
            FragColor = vec4(Color,1.0);
        }else{
            FragColor = vec4(Color,1.0)*max(1/max((FragPos.z-falloffDist)*falloffRate,1),minFalloff);
        }
    }else{
        discard;
    }
}