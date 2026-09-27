Enhanced Milk Vision is an OPTIONAL built-in resource pack.

It is registered with Fabric ResourcePackActivationType.NORMAL and is disabled
by default. With this pack inactive, FUNdoBREW renders Milk through the ordinary
FluidRenderingRegistry materials fundo:block/milk_still and fundo:block/milk_flow;
normal resource packs may replace those textures in the usual way.

With this pack active, its terrain shader recognizes Milk only through FUNdoBREW's
reserved fluid-vertex marker (and Milk Cauldrons through this pack's private
full-RGBA marker sprite). The generated world Milk and cauldron colours replace
the sampled raster colour with shader-owned white before lighting and noise are
applied. This intentionally keeps custom normal-fluid rasters out of the
Enhanced Milk Vision effect.
