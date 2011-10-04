package de.julianfeja.games.libgdx.graphics.texture;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.julianfeja.games.libgdx.input.ZipMultitextureInput;

public class TextureManager {
	private static TextureManager textureManager = null;

	protected Map<String, Texture> textures;
	protected Map<String, TextureAtlas> atlases;

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

	public static TextureManager instance() {
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
			TexturePath texturePath) {

		Pixmap pixmap = null;

		TextureObject ret = null;

		Array<Vector2> outline = null;

		if (texturePath.fileType == FileType.Image
				|| texturePath.fileType == FileType.SubImage) {
			pixmap = new Pixmap(Gdx.files.internal(assetPath));
		} else if (texturePath.fileType == FileType.Multitexture) {
			ZipMultitextureInput zipInput = new ZipMultitextureInput(
					texturePath.path);

			pixmap = zipInput.getPixmap();
			outline = zipInput.getOutline(texturePath.index);

			zipInput.close();
		}

		if (texturePath.bodyPolicy == BodyPolicy.Box) {
			ret = new BoxBodyTexture(pixmap, texturePath.path);
		} else if (texturePath.bodyPolicy == BodyPolicy.Parse) {
			ret = new ParsedBodyTexture(pixmap, texturePath.path);
		} else if (texturePath.bodyPolicy == BodyPolicy.Predefined) {
			ret = new SimpleOutlinedTextureObject(pixmap, outline,
					texturePath.path);
		}

		pixmap.dispose();

		return ret;
	}

	public TextureObject createTextureObject(String assetPath) {
		TexturePath texturePath = analysePath(assetPath);

		return createTextureObject(assetPath, texturePath);
	}

	public TextureObject createParsedBodyTexture(String assetPath) {
		TexturePath texturePath = analysePath(assetPath);

		texturePath.bodyPolicy = BodyPolicy.Parse;

		return createTextureObject(assetPath, texturePath);
	}

	public TextureObject createBoxBodyTexture(String assetPath) {
		TexturePath texturePath = analysePath(assetPath);

		texturePath.bodyPolicy = BodyPolicy.Box;

		return createTextureObject(assetPath, texturePath);
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

		String extension = path.substring(path.lastIndexOf('.') + 1,
				path.length());

		if (extension.toUpperCase().equals("PNG")
				|| extension.toUpperCase().equals("JPG")) {
			texturePath.fileType = FileType.Image;
			texturePath.bodyPolicy = BodyPolicy.Box;
		} else if (extension.toUpperCase().equals("ZIP")) {
			texturePath.fileType = FileType.Multitexture;
			texturePath.bodyPolicy = BodyPolicy.Predefined;
		}

		return texturePath;
	}
}
