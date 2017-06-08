#version 100
precision mediump float;
precision mediump int;

uniform sampler2D u_Texture;

varying vec2 v_UV1;
varying vec2 v_UV2;
varying vec2 v_UV3;
varying vec2 v_UV4;
varying vec2 v_UV5;

vec4 getBloom(in vec4 pixel)
{
	return *Filter*;
}

void main()
{
	gl_FragColor = getBloom(texture2D(u_Texture, v_UV1)) * 0.0702702703;
	gl_FragColor += getBloom(texture2D(u_Texture, v_UV2)) * 0.3162162162;
	gl_FragColor += getBloom(texture2D(u_Texture, v_UV3)) * 0.2270270270;
	gl_FragColor += getBloom(texture2D(u_Texture, v_UV4)) * 0.3162162162;
	gl_FragColor += getBloom(texture2D(u_Texture, v_UV5)) * 0.0702702703;
}

