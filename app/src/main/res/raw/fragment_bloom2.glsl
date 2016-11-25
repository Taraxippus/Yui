#version 100
precision mediump float;
precision mediump int;

uniform sampler2D u_Texture;

varying vec2 v_UV1;
varying vec2 v_UV2;
varying vec2 v_UV3;
varying vec2 v_UV4;
varying vec2 v_UV5;

void main()
{
	gl_FragColor = texture2D(u_Texture, v_UV1) * 0.06136;
	gl_FragColor += texture2D(u_Texture, v_UV2) * 0.24477;
	gl_FragColor += texture2D(u_Texture, v_UV3) * 0.38774;
	gl_FragColor += texture2D(u_Texture, v_UV4) * 0.24477;
	gl_FragColor += texture2D(u_Texture, v_UV5) * 0.06136;
}

