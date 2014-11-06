precision mediump float;

uniform float uTime;
uniform mat4 uMVPMatrix;
uniform sampler2D uTexture;

attribute vec4 aPosition;
attribute vec2 aTextureCoord;

varying vec2 vTextureCoord;
varying vec4 vColor;

void main(void)
{
	gl_Position = uMVPMatrix * aPosition;
	
	float frequence = 160.0;
    float damping = 1.0;          // smoothing value between 0.0 - 1.0
    float time = uTime*0.05;
    float radius = .5;
    float t = clamp(time / 6., 0., 1.);
    vec2 coords = vTextureCoord.st;
    vec2 dir = coords - vec2(.5);
    float dist = distance(coords, vec2(.5));

	vec2 offset = dir * (sin(dist * frequence - time*50.) + .5) / 60.;

	vec2 texCoord = coords + offset;
	
	vTextureCoord = texCoord;

}
