#version 300 es 					
precision highp float;
uniform sampler2D s_texture;
uniform float texMix;

in vec3 v_color;
in vec3 v_normal;
in vec2 v_texCoord;
layout(location = 0) out vec3 outColor;
void main()                                 
{
    outColor = mix(v_color, texture( s_texture, v_texCoord ).xyz*v_normal.z, texMix);
}

