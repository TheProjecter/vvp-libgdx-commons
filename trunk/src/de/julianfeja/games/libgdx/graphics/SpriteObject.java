package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class SpriteObject extends GeometricObject {
	protected Sprite sprite;

	public SpriteObject(Vector2 position, Vector2 scale, Sprite sprite) {
		super(position, scale);
		this.sprite = sprite;

		sprite.setOrigin(0, 0);

		sprite.setScale(scale.x * 1f / GeometricObject.PPM, scale.y * 1f
				/ GeometricObject.PPM);

		sprite.setPosition(position.x, position.y);
	}

	@Override
	public void paint(Camera camera, SpriteBatch batch) {
		sprite.draw(batch);
	}

	@Override
	public void dispose() {

	}

}
