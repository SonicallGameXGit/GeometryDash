package me.sgx.gd.graphics;

import me.sgx.engine.graphics.Window;
import me.sgx.engine.graphics.shader.Shader;
import me.sgx.engine.graphics.shader.ShaderProgram;
import me.sgx.engine.graphics.texture.FrameBuffer;
import me.sgx.engine.graphics.texture.Texture;
import me.sgx.engine.math.MathUtil;
import me.sgx.engine.mesh.Mesh;
import me.sgx.engine.mesh.MeshBuffer;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class Graphics {
	private static final Mesh mesh = new Mesh(new MeshBuffer(new float[] {
			-0.5f, -0.5f, 0.5f, -0.5f, -0.5f, 0.5f, 0.5f, 0.5f, -0.5f, 0.5f, 0.5f, -0.5f
	}, 2), null);

	private static final ShaderProgram shaderProgram = new ShaderProgram(), postShaderProgram = new ShaderProgram();
	private static FrameBuffer frameBuffer;

	public static void initialize() {
		shaderProgram.addShader(new Shader("res/shaders/world.vert", GL20.GL_VERTEX_SHADER));
		shaderProgram.addShader(new Shader("res/shaders/world.frag", GL20.GL_FRAGMENT_SHADER));
		shaderProgram.compile();

		postShaderProgram.addShader(new Shader("res/shaders/post.vert", GL20.GL_VERTEX_SHADER));
		postShaderProgram.addShader(new Shader("res/shaders/post.frag", GL20.GL_FRAGMENT_SHADER));
		postShaderProgram.compile();

		frameBuffer = new FrameBuffer(Window.getSize(), GL11.GL_LINEAR);
	}

	public static void begin() {
		mesh.load();
		frameBuffer.load();

		shaderProgram.load();
		shaderProgram.setInteger("colorSampler", 0);

		updateCamera();
	}
	public static void updateCamera() {
		shaderProgram.setMatrix4("project", MathUtil.orthographic(
				(float) Window.getWidth() / Window.getHeight(),
				1.0f / Camera.main.zoom.x(), 1.0f / Camera.main.zoom.y()
		));
		shaderProgram.setMatrix4("view", MathUtil.view(
				new Vector3f(Camera.main.position.x(), Camera.main.position.y(), 0.0f),
				new Vector3f(0.0f, 0.0f, Camera.main.rotation)
		));
	}

	public static void setTexture(int texture) {
		Texture.bind(texture, 0);
	}
	public static void setUv(Vector4f uv) {
		shaderProgram.setVector4("uv", uv);
	}
	public static void setColor(Vector4f color) {
		shaderProgram.setVector4("color", color);
	}
	public static void setTransform(Transform transform) {
		shaderProgram.setMatrix4("transform", MathUtil.transform(
				new Vector3f(transform.position.x(), transform.position.y(), 0.0f),
				new Vector3f(transform.size.x(), transform.size.y(), 1.0f),
				new Vector3f(0.0f, 0.0f, transform.rotation),
				new Vector3f(transform.anchor.x(), transform.anchor.y(), 0.0f)
		));
	}
	public static void setPostColor(Vector3f postColor) {
		postShaderProgram.setVector3("color", postColor);
	}

	public static void render() {
		mesh.render();
	}
	public static void end() {
		shaderProgram.unload();
		frameBuffer.unload();
	}
	public static void postBegin() {
		postShaderProgram.load();

		Texture.bind(frameBuffer.getTexture(), 0);
		postShaderProgram.setInteger("colorSampler", 0);
	}
	public static void postEnd() {
		mesh.render();
		postShaderProgram.unload();

		mesh.unload();
	}

	public static void clear() {
		Texture.clearAll();

		mesh.clear();
		frameBuffer.clear();
	}
}