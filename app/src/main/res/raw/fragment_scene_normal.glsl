#version 100
precision mediump float;
precision mediump int;

varying vec3 v_Normal;

void main()
{
	gl_FragColor = vec4(normalize(v_Normal) * 0.5 + vec3(0.5), 1.0);
}

