#version 100
precision mediump float;
precision mediump int;

attribute vec4 a_Position;
attribute vec3 a_Normal;

uniform mat4 u_MVP;
uniform mat4 u_N;

varying vec3 v_Normal;

void main()
{
	gl_Position = u_MVP * a_Position;
	v_Normal = vec4(u_N * vec4(a_Normal, 0.0)).xyz;
}
