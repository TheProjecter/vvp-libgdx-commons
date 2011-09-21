package de.julianfeja.games.libgdx.input;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.badlogic.gdx.Gdx;

public class ZipInput {
	protected Map<String, byte[]> content = new LinkedHashMap<String, byte[]>();

	public ZipInput(String assetPath) {
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(
				Gdx.files.internal(assetPath).read()));
		try {
			ZipEntry ze;
			while ((ze = zis.getNextEntry()) != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				byte[] buffer = new byte[1024];
				int count;
				while ((count = zis.read(buffer)) != -1) {
					baos.write(buffer, 0, count);
				}

				content.put(ze.getName(), baos.toByteArray());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			zis.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	protected byte[] getEntryAsByteArray(String entryName) {
		return content.get(entryName);
	}

	protected String getEntryAsString(String entryName) {
		byte[] bytes = content.get(entryName);

		return String.valueOf(bytes);
	}

	protected InputStream getEntryStream(String entryName) throws IOException {
		return new ByteArrayInputStream(content.get(entryName));
	}

	public void close() {

	}
}
