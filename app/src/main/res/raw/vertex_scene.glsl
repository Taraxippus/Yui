#version 100
precision highp float;
precision highp int;

attribute vec4 a_Position;

uniform mat4 u_MV;
uniform mat4 u_MVP;

varying vec3 v_Position;

void main()
{
	gl_Position = u_MVP * a_Position;
	v_Position = vec3(u_MV * a_Position);
}
