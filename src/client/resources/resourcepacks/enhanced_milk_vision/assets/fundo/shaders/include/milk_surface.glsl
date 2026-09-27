#ifndef FUNDO_MILK_SURFACE_GLSL
#define FUNDO_MILK_SURFACE_GLSL

 
 
 
 
float fundo_milk_hash(vec3 p) {
    p = fract(p * 0.1031);
    p += dot(p, p.yzx + 33.33);
    return fract((p.x + p.y) * p.z);
}

float fundo_milk_periodic_value_noise(vec3 p, vec3 period) {
    vec3 cell = mod(floor(p), period);
    vec3 local = fract(p);
    vec3 curve = local * local * (3.0 - 2.0 * local);

    float n000 = fundo_milk_hash(cell);
    float n100 = fundo_milk_hash(mod(cell + vec3(1.0, 0.0, 0.0), period));
    float n010 = fundo_milk_hash(mod(cell + vec3(0.0, 1.0, 0.0), period));
    float n110 = fundo_milk_hash(mod(cell + vec3(1.0, 1.0, 0.0), period));
    float n001 = fundo_milk_hash(mod(cell + vec3(0.0, 0.0, 1.0), period));
    float n101 = fundo_milk_hash(mod(cell + vec3(1.0, 0.0, 1.0), period));
    float n011 = fundo_milk_hash(mod(cell + vec3(0.0, 1.0, 1.0), period));
    float n111 = fundo_milk_hash(mod(cell + vec3(1.0, 1.0, 1.0), period));

    float lower = mix(mix(n000, n100, curve.x), mix(n010, n110, curve.x), curve.y);
    float upper = mix(mix(n001, n101, curve.x), mix(n011, n111, curve.x), curve.y);
    return mix(lower, upper, curve.z);
}

float fundo_milk_morphing_fbm(vec2 surfaceCoordinates, float cyclePhase) {
     
     
    const vec3 basePeriod = vec3(8.0);
    vec3 p = vec3(surfaceCoordinates, cyclePhase * basePeriod.z);

    float value = 0.58 * fundo_milk_periodic_value_noise(p + vec3(7.31, 2.17, 5.49), basePeriod);
    value += 0.244 * fundo_milk_periodic_value_noise(p * 2.0 + vec3(13.17, 4.61, 11.73), basePeriod * 2.0);
    value += 0.091 * fundo_milk_periodic_value_noise(p * 4.0 + vec3(3.73, 19.91, 7.07), basePeriod * 4.0);
    return value / 0.915;
}

 
 
 
vec2 fundo_milk_pixelate16(vec2 worldPlane) {
    return (floor(worldPlane * 16.0) + vec2(0.5)) * (1.0 / 16.0);
}

float fundo_milk_surface_noise(vec3 worldPosition, float gameTime, float faceKind) {
     
     
    float cyclePhase = fract(gameTime * 120.0);

    vec2 surfaceCoordinates;
    if (faceKind > 253.5) {
         
         
        surfaceCoordinates = fundo_milk_pixelate16(worldPosition.xz) * 1.35;
    } else if (faceKind > 252.5) {
         
        surfaceCoordinates = fundo_milk_pixelate16(vec2(worldPosition.x, worldPosition.y))
            * vec2(1.35, 2.4);
    } else {
         
        surfaceCoordinates = fundo_milk_pixelate16(vec2(worldPosition.z, worldPosition.y))
            * vec2(1.35, 2.4);
    }

     
    return fundo_milk_morphing_fbm(surfaceCoordinates * 1.16, cyclePhase);
}

 
 
 
bool fundo_is_milk_cauldron_marker(vec4 sampledColor) {
    return sampledColor.r > 0.96 && sampledColor.g < 0.05
        && sampledColor.b > 0.75 && sampledColor.a > 0.98 && sampledColor.a < 1.0;
}

#endif
