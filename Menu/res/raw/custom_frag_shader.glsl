precision mediump float;

varying vec2 vTextureCoord;
uniform sampler2D source;
uniform float uTime;
uniform float f;
uniform float f1;

void main() {

     const float twopi = 3.141592653589 * 2.0;

	 float f = sin(uTime*0.1); 
	 float f1 = cos(-uTime*0.1);
	 
	 float intensity = 2.0;	 
     float distanceFactorToPhase = pow(vTextureCoord.y + 0.5, 8.0) * 5.0;
     float ofx = sin(f * twopi + distanceFactorToPhase) / 100.0;
     float ofy = sin(f1 * twopi + distanceFactorToPhase * vTextureCoord.x) / 60.0;

     float intensityDampingFactor = (vTextureCoord.x + 0.5) * (vTextureCoord.y + 0.2);
     float distanceFactor = (1.0 - vTextureCoord.y) * 4.0 * intensity * intensityDampingFactor;

     ofx *= distanceFactor;
     ofy *= distanceFactor;

     float x = vTextureCoord.x + ofx;
     float y = 1.0 - vTextureCoord.y + ofy;

     float fake = (sin((ofy + ofx) * twopi) + 0.5) * 0.05 * (1.2 - vTextureCoord.y) * intensity * intensityDampingFactor;

     vec4 pix =
         texture2D(source, vec2(x, y)) * 0.6 +
         texture2D(source, vec2(x-fake, y)) * 0.15 +
         texture2D(source, vec2(x, y-fake)) * 0.15 +
         texture2D(source, vec2(x+fake, y)) * 0.15 +
         texture2D(source, vec2(x, y+fake)) * 0.15;

     float darken = 0.6 - (ofx - ofy) / 2.0;
     pix.b *= 1.2 * darken;
     pix.r *= 0.9 * darken;
     pix.g *= darken;

     gl_FragColor = vec4(pix.r, pix.g, pix.b, 1.0);
}
