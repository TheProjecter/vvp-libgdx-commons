package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class ParallaxBackgroundLayer {
	protected TextureRegion textureRegion;
	protected Rectangle drawRegion;
	protected Vector2 parallaxValues;

	public ParallaxBackgroundLayer(Texture texture, Rectangle textureRect, Rectangle drawRegion, Vector2 parallaxValues,
			Vector2 stdTextureDimension) {

		textureRegion = createTextureRegion(texture, textureRect, stdTextureDimension);
		this.drawRegion = drawRegion;
		this.parallaxValues = parallaxValues;
	}

	private static TextureRegion createTextureRegion(Texture texture, Rectangle textureRect, Vector2 stdTextureDimension) {
		textureRect.x /= stdTextureDimension.x;
		textureRect.y /= stdTextureDimension.y;

		textureRect.width = textureRect.width / stdTextureDimension.x + textureRect.x;
		textureRect.height = textureRect.height / stdTextureDimension.y + textureRect.y;

		return new TextureRegion(texture, textureRect.x, textureRect.y, textureRect.width, textureRect.height);
	}

	public TextureRegion getTextureRegion() {
		return textureRegion;
	}

	public Rectangle getDrawRegion() {
		return drawRegion;
	}

	public Vector2 getParallaxValues() {
		return parallaxValues;
	}
}
