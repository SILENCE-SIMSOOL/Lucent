package silence.simsool.lucent.general.utils.render;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class PrimitiveRenderer {

	public static void renderLineBox(PoseStack.Pose pose, VertexConsumer buffer, AABB aabb, float r, float g, float b, float a, float thickness) {
		renderLineBox(pose, buffer, (float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ, r, g, b, a, thickness);
	}

	public static void renderLineBox(PoseStack.Pose pose, VertexConsumer buffer, float x0, float y0, float z0, float x1, float y1, float z1, float r, float g, float b, float a, float thickness) {
		emitLine(pose, buffer, x0, y0, z0, x1, y0, z0, r, g, b, a, thickness);
		emitLine(pose, buffer, x1, y0, z0, x1, y0, z1, r, g, b, a, thickness);
		emitLine(pose, buffer, x1, y0, z1, x0, y0, z1, r, g, b, a, thickness);
		emitLine(pose, buffer, x0, y0, z1, x0, y0, z0, r, g, b, a, thickness);
		emitLine(pose, buffer, x0, y1, z0, x1, y1, z0, r, g, b, a, thickness);
		emitLine(pose, buffer, x1, y1, z0, x1, y1, z1, r, g, b, a, thickness);
		emitLine(pose, buffer, x1, y1, z1, x0, y1, z1, r, g, b, a, thickness);
		emitLine(pose, buffer, x0, y1, z1, x0, y1, z0, r, g, b, a, thickness);
		emitLine(pose, buffer, x0, y0, z0, x0, y1, z0, r, g, b, a, thickness);
		emitLine(pose, buffer, x1, y0, z0, x1, y1, z0, r, g, b, a, thickness);
		emitLine(pose, buffer, x1, y0, z1, x1, y1, z1, r, g, b, a, thickness);
		emitLine(pose, buffer, x0, y0, z1, x0, y1, z1, r, g, b, a, thickness);
	}

	private static void emitLine(PoseStack.Pose pose, VertexConsumer buffer, float x0, float y0, float z0, float x1, float y1, float z1, float r, float g, float b, float a, float thickness) {
		float dx = x1 - x0;
		float dy = y1 - y0;
		float dz = z1 - z0;
		buffer.addVertex(pose, x0, y0, z0).setColor(r, g, b, a).setNormal(pose, dx, dy, dz).setLineWidth(thickness);
		buffer.addVertex(pose, x1, y1, z1).setColor(r, g, b, a).setNormal(pose, dx, dy, dz).setLineWidth(thickness);
	}

	public static void addChainedFilledBoxVertices(PoseStack.Pose pose, VertexConsumer buffer, float minX, float minY, float minZ, float maxX, float maxY, float maxZ, float r, float g, float b, float a) {
		Matrix4f matrix = pose.pose();
		vertex(buffer, matrix, minX, minY, minZ, r, g, b, a);
		vertex(buffer, matrix, minX, minY, maxZ, r, g, b, a);
		vertex(buffer, matrix, minX, maxY, maxZ, r, g, b, a);
		vertex(buffer, matrix, minX, maxY, minZ, r, g, b, a);

		vertex(buffer, matrix, maxX, minY, maxZ, r, g, b, a);
		vertex(buffer, matrix, maxX, minY, minZ, r, g, b, a);
		vertex(buffer, matrix, maxX, maxY, minZ, r, g, b, a);
		vertex(buffer, matrix, maxX, maxY, maxZ, r, g, b, a);

		vertex(buffer, matrix, minX, minY, minZ, r, g, b, a);
		vertex(buffer, matrix, minX, maxY, minZ, r, g, b, a);
		vertex(buffer, matrix, maxX, maxY, minZ, r, g, b, a);
		vertex(buffer, matrix, maxX, minY, minZ, r, g, b, a);

		vertex(buffer, matrix, maxX, minY, maxZ, r, g, b, a);
		vertex(buffer, matrix, maxX, maxY, maxZ, r, g, b, a);
		vertex(buffer, matrix, minX, maxY, maxZ, r, g, b, a);
		vertex(buffer, matrix, minX, minY, maxZ, r, g, b, a);

		vertex(buffer, matrix, minX, minY, minZ, r, g, b, a);
		vertex(buffer, matrix, maxX, minY, minZ, r, g, b, a);
		vertex(buffer, matrix, maxX, minY, maxZ, r, g, b, a);
		vertex(buffer, matrix, minX, minY, maxZ, r, g, b, a);

		vertex(buffer, matrix, minX, maxY, maxZ, r, g, b, a);
		vertex(buffer, matrix, maxX, maxY, maxZ, r, g, b, a);
		vertex(buffer, matrix, maxX, maxY, minZ, r, g, b, a);
		vertex(buffer, matrix, minX, maxY, minZ, r, g, b, a);
	}

	private static void vertex(VertexConsumer buffer, Matrix4f matrix, float x, float y, float z, float r, float g, float b, float a) {
		buffer.addVertex(matrix, x, y, z).setColor(r, g, b, a);
	}

	public static void renderVector(PoseStack.Pose pose, VertexConsumer buffer, Vector3f start, Vec3 direction, int startColor, int endColor, float thickness) {
		float endX = start.x() + (float) direction.x;
		float endY = start.y() + (float) direction.y;
		float endZ = start.z() + (float) direction.z;
		float nx = (float) direction.x;
		float ny = (float) direction.y;
		float nz = (float) direction.z;

		buffer.addVertex(pose, start.x(), start.y(), start.z()).setColor(startColor).setNormal(pose, nx, ny, nz).setLineWidth(thickness);
		buffer.addVertex(pose, endX, endY, endZ).setColor(endColor).setNormal(pose, nx, ny, nz).setLineWidth(thickness);
	}

}