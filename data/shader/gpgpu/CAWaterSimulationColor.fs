// inspired by http://freespace.virgin.net/hugo.elias/graphics/x_water.htm

#extension GL_ARB_texture_rectangle : enable

uniform sampler2DRect read_cells;
uniform sampler2DRect read_prev_cells;
uniform sampler2DRect energy_map;
uniform vec2 flow_direction; // does not work yet
uniform float damping;

void main( void ) {
    	float s = gl_TexCoord[0].s;
    	float t = gl_TexCoord[0].t;

    	const float d = 1.0;

	    vec4 allColors = texture2DRect( read_cells, vec2( s - d, t) + flow_direction ) + 
	    	texture2DRect( read_cells, vec2( s + d, t) + flow_direction) +
	    	texture2DRect( read_cells, vec2( s, t - d) + flow_direction) +
	    	texture2DRect( read_cells, vec2( s, t + d) + flow_direction);

	// current height
	vec4 curr_height = texture2DRect( read_prev_cells, vec2( s, t ) );

	// new height
	vec4 new_heightVec = (allColors / 2.0 - curr_height) * damping; 

	// external energy
	vec4 ext_energy = texture2DRect( energy_map, vec2( s, t ) );
	new_heightVec += ext_energy;
	
	float max_ext_energy = ext_energy.r + ext_energy.g + ext_energy.b;
	// clamp
	new_heightVec = max (new_heightVec, -1.0);
	new_heightVec = min (new_heightVec, 1.0);

	if(max_ext_energy > 2.9){
		new_heightVec *= 0.0;
	}
    // output
    gl_FragColor = vec4(new_heightVec.r, new_heightVec.g, new_heightVec.b, 1 );
}
