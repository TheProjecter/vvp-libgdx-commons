package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class SpritePhysicsObject extends PhysicsObject {
	protected TextureObject textureObject;

	public SpritePhysicsObject(Vector2 position, Vector2 dimension,
			TextureObject textureObject, World world) {
		super(position, dimension, world);

		this.textureObject = textureObject;

		body = createBody();
	}

	@Override
	public void createFixtures(Body body) {

		for (PolygonShape bodyPoly : textureObject
				.createPolygonShapes(dimension)) {
			FixtureDef fixtureDef = new FixtureDef();
			fixtureDef.shape = bodyPoly;
			// fixtureDef.filter.groupIndex = 1; // 0 => no collision
			// // group,negative =>
			// // never collide, positive => always
			// // collide
			// fixtureDef.filter.categoryBits = 0x0001;
			// fixtureDef.filter.maskBits = 0x0001;
			// fixtureDef.isSensor = false;
			fixtureDef.restitution = 0.4f;
			fixtureDef.density = 1.0f;
			// fixtureDef.friction = 1.0f;

			body.createFixture(fixtureDef);
		}
	}

	@Override
	public void paint(SpriteBatch batch) {
		super.paint(batch);
		float angle = MathUtils.radiansToDegrees * body.getAngle();
		batch.draw(textureObject.getTexture(), position.x, position.y
				- dimension.y, 0f, dimension.y, dimension.x, dimension.y, 1f,
				1f, angle, 0, 0, 0, 0, false, false);

		batch.draw(new TextureRegion(textureObject.getTexture()), position.x,
				position.y - dimension.y, 0, dimension.y, dimension.x,
				dimension.y, 1.0f, 1.0f, angle);

	}

	@Override
	public void dispose() {
		super.dispose();

		textureObject.dispose();
	}

}
