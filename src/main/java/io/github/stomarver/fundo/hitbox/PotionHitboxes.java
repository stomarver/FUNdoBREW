package io.github.stomarver.fundo.hitbox;

import java.util.List;

import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public final class PotionHitboxes {

	private PotionHitboxes() {
	}

	public static final double SPLASH_WIDTH = 8.0D;

	public static final double SPLASH_RADIUS = SPLASH_WIDTH / 2.0D;

	public static final double MILK_SPLASH_WIDTH = 2.5D;

	public static final double CLOUD_HEIGHT = AreaEffectCloud.HEIGHT;

	public static final double MAX_CLOUD_RADIUS = 32.0D;

	public static Cylinder cloudCylinder(AreaEffectCloud cloud) {
		return new Cylinder(cloud.getX(), cloud.getY(), cloud.getZ(), cloud.getRadius(), CLOUD_HEIGHT);
	}

	public static AABB cloudCandidateBox(AABB subject) {
		return subject.inflate(MAX_CLOUD_RADIUS, CLOUD_HEIGHT, MAX_CLOUD_RADIUS);
	}

	public record Cylinder(double centerX, double baseY, double centerZ, double radius, double height) {

		public AABB broadBox() {
			return new AABB(centerX - radius, baseY, centerZ - radius,
					centerX + radius, baseY + height, centerZ + radius);
		}

		public boolean intersects(AABB box) {
			double dx = nearestCoordinate(centerX, box.minX, box.maxX) - centerX;
			double dz = nearestCoordinate(centerZ, box.minZ, box.maxZ) - centerZ;
			boolean within = dx * dx + dz * dz <= radius * radius;
			return within && box.maxY >= baseY && box.minY <= baseY + height;
		}
	}

	public record Sphere(double centerX, double centerY, double centerZ, double radius) {

		public static Sphere ofCenter(Vec3 center, double width) {
			return new Sphere(center.x, center.y, center.z, width / 2.0D);
		}

		public AABB broadBox() {
			return new AABB(centerX - radius, centerY - radius, centerZ - radius,
					centerX + radius, centerY + radius, centerZ + radius);
		}

		public boolean intersects(AABB box) {
			double dx = nearestCoordinate(centerX, box.minX, box.maxX) - centerX;
			double dy = nearestCoordinate(centerY, box.minY, box.maxY) - centerY;
			double dz = nearestCoordinate(centerZ, box.minZ, box.maxZ) - centerZ;
			return dx * dx + dy * dy + dz * dz <= radius * radius;
		}
	}

	public static List<LivingEntity> collect(Level level, Cylinder cylinder) {
		return select(level, cylinder);
	}

	public static List<LivingEntity> collect(Level level, Sphere sphere) {
		return select(level, sphere);
	}

	private static List<LivingEntity> select(Level level, Object shape) {
		AABB broad;
		switch (shape) {
			case Cylinder cylinder -> broad = cylinder.broadBox();
			case Sphere sphere -> broad = sphere.broadBox();
			default -> throw new IllegalArgumentException("unknown radial shape: " + shape);
		}
		List<LivingEntity> candidates = level.getEntitiesOfClass(LivingEntity.class, broad);
		candidates.removeIf(entity -> {
			AABB box = entity.getBoundingBox();
			return switch (shape) {
				case Cylinder cylinder -> !cylinder.intersects(box);
				case Sphere sphere -> !sphere.intersects(box);
				default -> true;
			};
		});
		return candidates;
	}

	public static double nearestCoordinate(double p, double min, double max) {
		return p < min ? min : Math.min(p, max);
	}
}
