#version 100
precision mediump float;
precision mediump int;

attribute vec4 a_Position;
attribute vec4 a_Color;
attribute vec2 a_Direction;

uniform mat4 u_MV;
uniform mat4 u_P;

varying vec4 v_Color;
varying vec2 v_UV;

void main()
{
	v_Color = a_Color;
	v_UV = a_Direction * -0.5 + vec2(0.5, 0.5);

	gl_Position = u_P * (vec4(a_Direction * a_Position.w, 0.0, 0.0) + u_MV * vec4(a_Position.xyz, 1.0));
}
