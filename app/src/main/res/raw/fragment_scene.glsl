#version 100
precision mediump float;
precision mediump int;

uniform vec4 u_Color;
uniform float u_Fog;

void main()
{
	gl_FragColor = vec4(mix(u_Color.rgb, vec3(1.0), 1.0 - exp(-(gl_FragCoord.z / gl_FragCoord.w * gl_FragCoord.z / gl_FragCoord.w) * u_Fog)), u_Color.a);
}

