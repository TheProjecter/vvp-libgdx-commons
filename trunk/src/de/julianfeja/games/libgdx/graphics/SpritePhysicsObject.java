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

	public SpritePhysicsObject(Vector2 position, Vector2 scale,
			TextureObject textureObject, World world) {
		super(position, scale, new Vector2(textureObject.getDimension().x
				* scale.x, textureObject.getDimension().y * scale.y), world);

		this.textureObject = textureObject;

		body = createBody();
	}

	public SpritePhysicsObject(Vector2 position, float scale,
			TextureObject textureObject, World world) {

		this(position, new Vector2(scale, scale), textureObject, world);
	}

	public SpritePhysicsObject(Vector2 position, TextureObject textureObject,
			World world) {
		this(position, 1.0f, textureObject, world);
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

		Rectangle rect = textureObject.getRect();
		Vector2 pixmapDimension = new Vector2(rect.width / GeometricObject.PPM,
				rect.height / GeometricObject.PPM);
		Vector2 rectPos = new Vector2(rect.x / GeometricObject.PPM, -rect.y
				/ GeometricObject.PPM - pixmapDimension.y);

		batch.draw(new TextureRegion(textureObject.getTexture(), rect.x,
				rect.y, rect.width, rect.height), position.x + rectPos.x,
				position.y + rectPos.y, -rectPos.x, -rectPos.y,
				pixmapDimension.x, pixmapDimension.y, 1f, 1f, angle);

	}

	@Override
	public void dispose() {
		super.dispose();

		textureObject.dispose();
	}

}
