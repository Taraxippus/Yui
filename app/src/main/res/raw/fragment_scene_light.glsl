#version 100
precision mediump float;
precision mediump int;

uniform vec4 u_Light;
uniform vec4 u_Color;
uniform vec4 u_Fog;

varying vec3 v_Normal;

void main()
{
	gl_FragColor = vec4(mix((dot(normalize(v_Normal), -u_Light.xyz) * (1.0 - u_Light.a) + u_Light.a) * u_Color.rgb, u_Fog.rgb, 1.0 - exp(-(gl_FragCoord.z / gl_FragCoord.w * gl_FragCoord.z / gl_FragCoord.w) * u_Fog.w)), u_Color.a);
}

