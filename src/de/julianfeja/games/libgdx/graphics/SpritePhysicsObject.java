package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;

public class SpritePhysicsObject extends PhysicsObject {
	protected TextureObject textureObject;

	public SpritePhysicsObject(Vector2 position, Vector2 dimension, TextureObject textureObject, World world) {
		super(position, dimension, world);

		this.textureObject = textureObject;
	}

	@Override
	public Shape createShape() {
		PolygonShape retVal = new PolygonShape();

		Vector2[] vecs = new Vector2[] { new Vector2(0, 5), new Vector2(0, 0), new Vector2(5, 5) };

		retVal.set(vecs);

		return retVal;
	}

	@Override
	public void paint(SpriteBatch batch) {
		super.paint(batch);
		float angle = MathUtils.radiansToDegrees * body.getAngle();
		batch.draw(textureObject.getTextureRegion(), position.x - dimension.x / 2, position.y - dimension.y / 2, dimension.x / 2,
				dimension.y / 2, dimension.x, dimension.y, 1.0f, 1.0f, angle);
	}

}
