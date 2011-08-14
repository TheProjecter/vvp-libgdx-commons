package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureObject {
	protected String assetPath;
	protected TextureRegion textureRegion;

	public TextureObject(String assetPath) {
		textureRegion = new TextureRegion(new Texture(Gdx.files.internal(assetPath)));
	}

	public TextureRegion getTextureRegion() {
		return textureRegion;
	}

	public String getAssetPath() {
		return assetPath;
	}
}
