#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect heightfield;
uniform float contrast;

void main( void ) {
    // black on white
    //const float contrast = 1.3;
    vec2 myCoord = vec2(gl_TexCoord[0].s, gl_TexCoord[0].t);

    	// take absolut value of current height and scale it a bit
    	//float intensity = contrast *abs( texture2DRect( heightfield, myCoord ).r );
    	vec4 intensity = contrast *abs( texture2DRect( heightfield, myCoord ) );
    
    	// invert to white on black
	//intensity = 1.0 - intensity;

    gl_FragColor = vec4(intensity.r, intensity.g, intensity.b, 1);
}

