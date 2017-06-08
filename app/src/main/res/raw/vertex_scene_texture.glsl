#version 100
precision mediump float;
precision mediump int;

attribute vec4 a_Position;
attribute vec2 a_UV;

uniform mat4 u_MVP;

varying vec2 v_UV;

void main()
{
	gl_Position = u_MVP * a_Position;
  v_UV = a_UV;
}
