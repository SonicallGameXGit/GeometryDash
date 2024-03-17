package me.sgx.gd.graphics;

import me.sgx.engine.graphics.Window;
import me.sgx.engine.graphics.shader.Shader;
import me.sgx.engine.graphics.shader.ShaderProgram;
import me.sgx.engine.graphics.texture.Texture;
import me.sgx.engine.math.MathUtil;
import me.sgx.engine.mesh.Mesh;
import me.sgx.engine.mesh.MeshBuffer;
import me.sgx.gd.world.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public abstract class Drawable {
	private static final Mesh mesh = new Mesh(new MeshBuffer(new float[] { -0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f }, 2), null, GL11.GL_TRIANGLES);
	private static final ShaderProgram worldShader = new ShaderProgram();

	private static final Vector4f bounds = new Vector4f();

	public static void initialize() {
		worldShader.addShader(new Shader("res/shaders/world.vs", GL20.GL_VERTEX_SHADER));
		worldShader.addShader(new Shader("res/shaders/world.fs", GL20.GL_FRAGMENT_SHADER));
		worldShader.compile();
	}

	public static void begin(Vector2f viewPosition, Vector2f viewZoom, float viewRotation) {
		mesh.load();

		float aspect = (float) Window.getWidth() / Window.getHeight();
		bounds.x = viewPosition.x() - aspect / viewZoom.x();
		bounds.z = viewPosition.x() + aspect / viewZoom.x();
		bounds.y = viewPosition.y() - 1.0f / viewZoom.y();
		bounds.w = viewPosition.y() + 1.0f / viewZoom.y();

		worldShader.load();
		worldShader.setUniform("project", MathUtil.orthographic(aspect, 1.0f / viewZoom.x(), 1.0f / viewZoom.y()));
		worldShader.setUniform("view", MathUtil.view(new Vector3f(viewPosition.x(), viewPosition.y(), 0.0f), new Vector3f(0.0f, 0.0f, viewRotation)));
		worldShader.setUniform("colorSampler", 0);
	}
	public static void end() {
		worldShader.unload();
		mesh.unload();
	}
	public static void loadTexture(int texture) {
		Texture.load(texture, 0);
	}

	public abstract void render();

	protected void render(Transform transform, Vector2f anchor, Vector4f uv) {
		worldShader.setUniform("transform", MathUtil.transform(new Vector3f(transform.position.x(), transform.position.y(), 0.0f), new Vector3f(transform.scale.x(), transform.scale.y(), 1.0f), new Vector3f(0.0f, 0.0f, transform.rotation), new Vector3f(anchor.x(), anchor.y(), 0.0f)));
		worldShader.setUniform("uv", uv);

		mesh.render();
	}
	protected void render(Transform transform) {
		render(transform, new Vector2f(), new Vector4f(0.0f, 0.0f, 1.0f, 1.0f));
	}
	protected void render(Transform transform, Vector2f anchor) {
		render(transform, anchor, new Vector4f(0.0f, 0.0f, 1.0f, 1.0f));
	}
	protected void render(Transform transform, Vector4f uv) {
		render(transform, new Vector2f(), uv);
	}

	public static Vector4f getBounds() {
		return new Vector4f(bounds);
	}
}