package de.julianfeja.games.libgdx.graphics;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class TextureManager {
	private static TextureManager textureManager = null;

	protected Map<String, Texture> textures;

	public enum FileType {
		Image, SubImage, Multitexture
	}

	public enum BodyPolicy {
		Box, Parse, Predefined
	}

	protected class TexturePath {
		public String path;
		public int index = 0;
		public FileType fileType = FileType.Image;
		public BodyPolicy bodyPolicy = BodyPolicy.Box;
	}

	private TextureManager() {
		textures = new HashMap<String, Texture>();
	}

	static TextureManager instance() {
		if (textureManager != null) {
			return textureManager;
		} else {
			return new TextureManager();
		}
	}

	public Texture getTexture(String assetPath) {
		return createTexture(assetPath, null);
	}

	public Texture createTexture(String assetPath, Pixmap pixmap) {
		if (textures.containsKey(assetPath)) {
			return textures.get(assetPath);
		} else {
			Texture texture;
			if (pixmap != null) {
				texture = new Texture(pixmap);
			} else {
				texture = new Texture(assetPath);
			}

			textures.put(assetPath, texture);

			return texture;
		}
	}

	public TextureObject createTextureObject(String assetPath,
			FileType fileType, BodyPolicy bodyPolicy) {

		return null;
	}

	public TextureObject createTextureObject(String assetPath) {
		TexturePath texturePath = analysePath(assetPath);

		return null;
	}

	protected TexturePath analysePath(String path) {
		String[] splitted = path.split("#");
		int index = 0;

		path = splitted[0];

		if (splitted.length > 1) {
			index = Integer.parseInt(splitted[1]);
		}

		TexturePath texturePath = new TexturePath();

		texturePath.path = path;
		texturePath.index = index;

		String extension = path.substring(path.lastIndexOf('.'), path.length());

		if (extension.toUpperCase().equals("PNG")
				|| extension.toUpperCase().equals("JPG")) {
			texturePath.fileType = FileType.Image;
		} else if (extension.toUpperCase().equals("ZIP")) {
			texturePath.fileType = FileType.Multitexture;
		}

		return texturePath;
	}
}
