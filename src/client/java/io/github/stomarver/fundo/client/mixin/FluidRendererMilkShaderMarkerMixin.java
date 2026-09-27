package io.github.stomarver.fundo.client.mixin;

import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.stomarver.fundo.fluid.FundoFluids;
import net.minecraft.client.renderer.block.BlockAndTintGetter;
import net.minecraft.client.renderer.block.FluidRenderer;
import net.minecraft.client.renderer.chunk.ChunkSectionLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ARGB;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

 






@Mixin(FluidRenderer.class)
public abstract class FluidRendererMilkShaderMarkerMixin {
	@Redirect(
			method = "tesselate",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/block/FluidRenderer$Output;getBuilder(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayer;)Lcom/mojang/blaze3d/vertex/VertexConsumer;")
	)
	private VertexConsumer fundo$markMilkVertices(FluidRenderer.Output output, ChunkSectionLayer layer,
			BlockAndTintGetter level, BlockPos pos, FluidRenderer.Output ignoredOutput,
			BlockState state, FluidState fluidState) {
		VertexConsumer delegate = output.getBuilder(layer);
		return FundoFluids.MILK != null && fluidState.getType().isSame(FundoFluids.MILK)
				? new MilkMarkerVertexConsumer(delegate)
				: delegate;
	}

	 






	@Inject(method = "addFace", at = @At("HEAD"))
	private void fundo$selectMilkSurfaceMapping(VertexConsumer vertices,
			float x0, float y0, float z0, float u0, float v0,
			float x1, float y1, float z1, float u1, float v1,
			float x2, float y2, float z2, float u2, float v2,
			float x3, float y3, float z3, float u3, float v3,
			int color, int light, boolean renderBackface, CallbackInfo callback) {
		if (vertices instanceof MilkMarkerVertexConsumer milkVertices) {
			milkVertices.beginFace(x0, y0, z0, x1, y1, z1, x2, y2, z2);
		}
	}

	private static final class MilkMarkerVertexConsumer implements VertexConsumer {
		 
		 
		private static final int MILK_TOP_MARKER_ALPHA = 254;
		private static final int MILK_NORTH_SOUTH_MARKER_ALPHA = 253;
		private static final int MILK_EAST_WEST_MARKER_ALPHA = 252;

		private final VertexConsumer delegate;
		private int faceMarkerAlpha = MILK_TOP_MARKER_ALPHA;

		private MilkMarkerVertexConsumer(VertexConsumer delegate) {
			this.delegate = delegate;
		}

		private void beginFace(float x0, float y0, float z0, float x1, float y1, float z1,
				float x2, float y2, float z2) {
			float edgeAX = x1 - x0;
			float edgeAY = y1 - y0;
			float edgeAZ = z1 - z0;
			float edgeBX = x2 - x0;
			float edgeBY = y2 - y0;
			float edgeBZ = z2 - z0;
			float normalX = edgeAY * edgeBZ - edgeAZ * edgeBY;
			float normalY = edgeAZ * edgeBX - edgeAX * edgeBZ;
			float normalZ = edgeAX * edgeBY - edgeAY * edgeBX;

			float absX = Math.abs(normalX);
			float absY = Math.abs(normalY);
			float absZ = Math.abs(normalZ);
			 
			 
			faceMarkerAlpha = absY >= absX && absY >= absZ
					? MILK_TOP_MARKER_ALPHA
					: absZ >= absX ? MILK_NORTH_SOUTH_MARKER_ALPHA : MILK_EAST_WEST_MARKER_ALPHA;
		}

		@Override
		public VertexConsumer addVertex(float x, float y, float z) {
			delegate.addVertex(x, y, z);
			return this;
		}

		@Override
		public VertexConsumer setColor(int red, int green, int blue, int alpha) {
			delegate.setColor(red, green, blue, faceMarkerAlpha);
			return this;
		}

		@Override
		public VertexConsumer setColor(int color) {
			delegate.setColor(ARGB.color(faceMarkerAlpha, ARGB.red(color), ARGB.green(color), ARGB.blue(color)));
			return this;
		}

		@Override
		public VertexConsumer setUv(float u, float v) {
			delegate.setUv(u, v);
			return this;
		}

		@Override
		public VertexConsumer setUv1(int u, int v) {
			delegate.setUv1(u, v);
			return this;
		}

		@Override
		public VertexConsumer setUv2(int u, int v) {
			delegate.setUv2(u, v);
			return this;
		}

		@Override
		public VertexConsumer setUv3(float u, float v) {
			delegate.setUv3(u, v);
			return this;
		}

		@Override
		public VertexConsumer setNormal(float x, float y, float z) {
			delegate.setNormal(x, y, z);
			return this;
		}

		@Override
		public VertexConsumer setLineWidth(float width) {
			delegate.setLineWidth(width);
			return this;
		}
	}
}
