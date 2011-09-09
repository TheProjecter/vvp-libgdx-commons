package de.julianfeja.games.libgdx.input;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Array;

public class ZipInput {
	protected ZipFile zipFile;

	protected Array<ZipEntry> entries = new Array<ZipEntry>();

	public ZipInput(String assetPath) {
		try {
			URL url = FileHandle.class.getResource(assetPath);
			if (url == null) {
				throw new FileNotFoundException();
			}

			File file = new File(url.toURI());

			zipFile = new ZipFile(file);

		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected byte[] getEntryAsByteArray(String entryName) throws IOException {
		ZipEntry entry = zipFile.getEntry(entryName);

		ByteArrayOutputStream buffer = new ByteArrayOutputStream();

		InputStream is = zipFile.getInputStream(entry);

		int nRead;
		byte[] data = new byte[16384];

		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		buffer.flush();
		is.close();

		return buffer.toByteArray();
	}

	protected String getEntryAsString(String entryName) throws IOException {
		ZipEntry entry = zipFile.getEntry(entryName);
		StringBuilder stringBuilder = new StringBuilder();

		InputStream is = zipFile.getInputStream(entry);

		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(is));

		String line = null;

		while ((line = bufferedReader.readLine()) != null) {
			stringBuilder.append(line + "\n");
		}

		bufferedReader.close();

		return stringBuilder.toString();
	}

	public void close() {
		try {
			zipFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
