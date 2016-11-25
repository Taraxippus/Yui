#version 100
precision mediump float;
precision mediump int;

attribute vec2 a_Position;

uniform vec2 u_InvResolution;

varying vec2 v_UV;
varying vec2 v_UV_NW;
varying vec2 v_UV_NE;
varying vec2 v_UV_SW;
varying vec2 v_UV_SE;

void main()
{
	v_UV = a_Position * 0.5 + vec2(0.5, 0.5);
	v_UV_NW = v_UV.xy + (vec2(-1.0, -1.0) * u_InvResolution);
	v_UV_NE = v_UV.xy + (vec2(+1.0, -1.0) * u_InvResolution);
	v_UV_SW = v_UV.xy + (vec2(-1.0, +1.0) * u_InvResolution);
	v_UV_SE = v_UV.xy + (vec2(+1.0, +1.0) * u_InvResolution);
	
	gl_Position = vec4(a_Position, 0.0, 1.0);
}
