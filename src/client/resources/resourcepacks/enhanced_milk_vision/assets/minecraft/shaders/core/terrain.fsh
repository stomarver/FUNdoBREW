#version 330
#extension GL_ARB_separate_shader_objects : require

#include <minecraft:fog.glsl>
#include <minecraft:globals.glsl>
#include <minecraft:texture_sampling.glsl>
#include <minecraft:oit.glsl>
#include <minecraft:terrainglobals.glsl>
#include <fundo:milk_surface.glsl>
#ifndef MULTIDRAW_TERRAIN
    #include <minecraft:chunksection.glsl>
#endif

uniform sampler2D Sampler0;

layout(location = 0) in float sphericalVertexDistance;
layout(location = 1) in float cylindricalVertexDistance;
layout(location = 2) in vec4 vertexColor;
layout(location = 3) in vec2 texCoord0;
layout(location = 4) in float chunkVisibility;
layout(location = 5) in vec3 fundoWorldPosition;
layout(location = 6) flat in float fundoMilkSurfaceKind;

#ifndef OIT_ALPHA_ONLY
layout(location = 0) out vec4 fragColor;
#endif

vec4 calculateFinalColor(vec4 color) {
    #ifdef OIT_ACCUMULATE
    color = sampleColorForAccumulation(color);
    vec4 fogColor = vec4(FogColor.rgb * color.a, FogColor.a);
    #else
    vec4 fogColor = FogColor;
    #endif
    return apply_fog(color, sphericalVertexDistance, cylindricalVertexDistance, FogEnvironmentalStart, FogEnvironmentalEnd, FogRenderDistanceStart, FogRenderDistanceEnd, fogColor);
}

void main() {
    vec4 sampledColor = UseRgss == 1
        ? sampleRGSS(Sampler0, texCoord0, 1.0f / TextureSize)
        : sampleNearest(Sampler0, texCoord0, 1.0f / TextureSize);
     
     
     
    bool fundoMilkCauldron = fundo_is_milk_cauldron_marker(sampledColor);
    vec4 color = sampledColor * vertexColor;
    #ifndef OIT_ALPHA_ONLY
    color = mix(FogColor * vec4(1, 1, 1, color.a), color, chunkVisibility);
    #endif

    if (fundoMilkSurfaceKind > 0.5 || fundoMilkCauldron) {
         
         
         
         
        float faceKind = fundoMilkCauldron ? 254.0 : fundoMilkSurfaceKind;
        float noise = fundo_milk_surface_noise(fundoWorldPosition, GameTime, faceKind);
         
         
         
        const vec3 fundoMilkDeepBase = vec3(0.819, 0.8055, 0.7695);
        const float fundoMilkDeepLightenGain = 1.20;
        vec3 fundoMilkDeepLightened = min(vec3(1.0), fundoMilkDeepBase * fundoMilkDeepLightenGain);
        const vec3 fundoMilkLuminance = vec3(0.2126, 0.7152, 0.0722);
        vec3 fundoMilkDeepNeutral = vec3(dot(fundoMilkDeepLightened, fundoMilkLuminance));
         
         
        const float fundoMilkDeepSaturationGain = 1.5625;
        vec3 fundoMilkDeepShade = mix(fundoMilkDeepNeutral, fundoMilkDeepLightened,
                fundoMilkDeepSaturationGain);
         
         
         
         
        const float fundoMilkHighlightGain = 1.30;
        float fundoMilkHighlightNoise = min(1.0, noise * fundoMilkHighlightGain);
        vec3 dairyShade = mix(fundoMilkDeepShade, vec3(1.0), fundoMilkHighlightNoise);
         
         
         
         
        const vec3 fundoMilkShaderWhite = vec3(1.0);
        vec3 milkBase = fundoMilkShaderWhite * vertexColor.rgb;
        #ifndef OIT_ALPHA_ONLY
        milkBase = mix(FogColor.rgb, milkBase, chunkVisibility);
        #endif
        color.rgb = milkBase * dairyShade;
        color.a = 1.0;
    }

    #ifdef ALPHA_CUTOUT
    if (color.a < ALPHA_CUTOUT) {
        discard;
    }
    #endif

    #ifdef OIT_ALPHA_ONLY
    executeAlphaOnlyPhase(gl_FragCoord.z, color.a);
    #else
    fragColor = calculateFinalColor(color);
    #endif
}
