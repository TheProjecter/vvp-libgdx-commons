package de.julianfeja.games.libgdx.input;

import java.io.IOException;

import com.badlogic.gdx.graphics.Pixmap;

public class ZipMultitextureInput extends ZipInput {
	protected Pixmap pixmap;

	public ZipMultitextureInput(String assetPath) {
		super(assetPath);

		try {
			byte[] textureByteArray = getEntryAsByteArray("texture.png");

			pixmap = new Pixmap(textureByteArray, 0, textureByteArray.length);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
