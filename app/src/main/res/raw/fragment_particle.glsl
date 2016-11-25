#version 100
precision mediump float;
precision mediump int;

uniform sampler2D u_Texture;

varying vec4 v_Color;
varying vec2 v_UV;

void main()
{
	gl_FragColor = texture2D(u_Texture, v_UV) * v_Color;
}

