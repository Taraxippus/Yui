#version 100
precision highp float;
precision highp int;

uniform vec4 u_Color;
uniform vec4 u_Fog;
uniform sampler2D u_Dither;

varying vec3 v_Position;

void main()
{
	float dist = length(v_Position);
	gl_FragColor = vec4(mix(u_Color.rgb, u_Fog.rgb, 1.0 - exp(-dist * dist * u_Fog.w)), u_Color.a);
}

