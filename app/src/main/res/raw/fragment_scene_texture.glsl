#version 100
precision mediump float;
precision mediump int;

uniform vec4 u_Color;
uniform vec4 u_Fog;
uniform sampler2D u_Texture;

varying vec2 v_UV;

void main()
{
  	gl_FragColor = texture2D(u_Texture, v_UV) * u_Color;
	gl_FragColor = vec4(mix(gl_FragColor.rgb, u_Fog.rgb, 1.0 - exp(-(gl_FragCoord.z / gl_FragCoord.w * gl_FragCoord.z / gl_FragCoord.w) * u_Fog.w)), gl_FragColor.a);
}

