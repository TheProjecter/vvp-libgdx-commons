package de.julianfeja.games.libgdx.graphics;

import java.util.HashMap;
import java.util.Map;

public class TextureManager {
	protected Map<String, TextureObject> textures;

	public TextureManager() {
		textures = new HashMap<String, TextureObject>();
	}

	public TextureObject getTextureObject(String assetPath) {
		if (textures.containsKey(assetPath)) {
			return textures.get(assetPath);
		} else {
			TextureObject textureObject = new TextureObject(assetPath);

			textures.put(assetPath, textureObject);

			return textureObject;
		}
	}
}
