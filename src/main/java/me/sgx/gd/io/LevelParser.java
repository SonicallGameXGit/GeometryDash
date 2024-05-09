package me.sgx.gd.io;

import lombok.extern.log4j.Log4j2;
import me.sgx.gd.world.World;
import me.sgx.gd.world.block.Block;
import me.sgx.gd.world.block.Blocks;
import me.sgx.gd.world.block.PlacedBlock;
import me.sgx.gd.world.math.Transform;
import org.joml.Vector2f;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

@Log4j2
public class LevelParser {
	public static void load(String location) {
        log.info("Parsing level {}", location);
		byte[] bytes;
		try {
			InflaterInputStream fileInputStream = new InflaterInputStream(new FileInputStream(location));
			World.blocks.clear();

			bytes = fileInputStream.readAllBytes();
			for(int i = 0; i < bytes.length / 21; i++) {
				byte[] position = new byte[8], scale = new byte[8], rotation = new byte[4];

				System.arraycopy(bytes, i * 21 + 1, position, 0, position.length);
				System.arraycopy(bytes, i * 21 + 9, scale, 0, scale.length);
				System.arraycopy(bytes, i * 21 + 17, rotation, 0, rotation.length);

				Block block = Blocks.getById(bytes[i * 21]);
				if(block == null) continue;

				World.blocks.add(new PlacedBlock(block, new Transform(
						byteArrayToVector2f(position),
						byteArrayToVector2f(scale),
						byteArrayToFloat(rotation)
				)));
			}
		} catch(IOException exception) {
			throw new RuntimeException("An error occurred while reading level file", exception);
		}

		String[] files = location.replace('\\', '/').split("/");
		World.loadSong(files[files.length - 1].split("\\.")[0]);

		World.time.update();
	}
	public static void save(String location) {
		log.info("Saving level to: {}", location);
		File file = new File(location);

		//noinspection ResultOfMethodCallIgnored
		file.getParentFile().mkdirs();

		try {
			//noinspection ResultOfMethodCallIgnored
			file.createNewFile();

			DeflaterOutputStream fileOutputStream = new DeflaterOutputStream(new FileOutputStream(file));
			for(PlacedBlock block : World.blocks) {
				byte[] result = new byte[21]; // 1[id] + 8[position] + 8[scale] + 4[rotation]
				result[0] = block.block.id;

				System.arraycopy(byteArrayFromVector2f(block.transform.position), 0, result, 1, 8);
				System.arraycopy(byteArrayFromVector2f(block.transform.size), 0, result, 9, 8);
				System.arraycopy(floatToByteArray(block.transform.rotation), 0, result, 17, 4);

				fileOutputStream.write(result);
			}

			fileOutputStream.flush();
			fileOutputStream.close();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private static byte[] floatToByteArray(float value) {
		int bits = Float.floatToIntBits(value);
		return new byte[] { (byte) (bits >> 24), (byte) (bits >> 16), (byte) (bits >> 8), (byte) bits };
	}

	private static float byteArrayToFloat(byte[] bytes) {
		return Float.intBitsToFloat(
				bytes[0] << 24 | (bytes[1] & 0xFF) << 16 | (bytes[2] & 0xFF) << 8 | (bytes[3] & 0xFF)
		);
	}

	private static byte[] byteArrayFromVector2f(Vector2f value) {
		byte[] x = floatToByteArray(value.x());
		byte[] y = floatToByteArray(value.y());

		byte[] result = new byte[x.length + y.length];
		System.arraycopy(x, 0, result, 0, x.length);
		System.arraycopy(y, 0, result, x.length, y.length);

		return result;
	}
	private static Vector2f byteArrayToVector2f(byte[] bytes) {
		byte[] x = new byte[4];
		byte[] y = new byte[4];

		System.arraycopy(bytes, 0, x, 0, x.length);
		System.arraycopy(bytes, 4, y, 0, y.length);

		return new Vector2f(byteArrayToFloat(x), byteArrayToFloat(y));
	}
}