#version 100
precision mediump float;
precision mediump int;

uniform vec4 u_Color;
uniform vec4 u_Fog;

void main()
{
	gl_FragColor = vec4(mix(u_Color.rgb, u_Fog.rgb, 1.0 - exp(-(gl_FragCoord.z / gl_FragCoord.w * gl_FragCoord.z / gl_FragCoord.w) * u_Fog.w)), u_Color.a);
}

