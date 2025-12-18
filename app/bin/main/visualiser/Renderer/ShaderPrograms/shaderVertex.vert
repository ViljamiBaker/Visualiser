#version 330 core
layout (location = 0) in vec3 aPos;
//layout (location = 1) in vec3 aNormal;
//layout (location = 2) in vec2 aTexCoords;

out vec3 FragPos;
out vec3 Color;
out vec3 Center;
//out vec3 Normal;
//out vec2 TexCoords;

uniform vec3 pos;
uniform vec3 color;
uniform mat4 view;
uniform mat4 projection;
uniform float sr;

void main()
{
    vec4 p = projection * view * vec4(pos.x,pos.y,pos.z, 1.0)+vec4(aPos.x,aPos.y*sr,0.0,1.0)*0.1;
    FragPos = vec3(p);
    Center = vec3(projection * view * vec4(pos, 1.0));
    Color = color;
    //Normal = mat3(transpose(inverse(model))) * aNormal;  
    //TexCoords = aTexCoords;
    
    gl_Position = p;
}