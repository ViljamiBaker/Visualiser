#version 330 core

out vec4 FragColor;
in vec3 Color;
in vec3 FragPos;

uniform float falloffRate;
uniform float falloffDist;
uniform float minFalloff;

void main()
{
    if(minFalloff==1){
        FragColor = vec4(Color,1.0);
    }else{
        FragColor = vec4(Color,1.0)*max(1/max((FragPos.z-falloffDist)*falloffRate,1),minFalloff);
    }
}