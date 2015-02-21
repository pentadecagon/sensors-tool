#version 300 es
uniform mat4 perspectiveMatrix;
uniform mat4 rotMatrix;
layout(location = 0) in vec4 a_position;
layout(location = 1) in vec4 a_color;
layout(location = 2) in vec3 a_normal;
layout(location = 3) in vec2 a_texCoord;
out vec2 v_texCoord;
out vec3 v_color;
out vec3 v_normal;
void main()
{
  v_texCoord = a_texCoord;
   v_color = a_normal;
   v_normal = mat3(rotMatrix) * a_normal;
   gl_Position = perspectiveMatrix * rotMatrix * a_position;
}

