package de.julianfeja.games.libgdx.graphics.physical;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

import de.julianfeja.games.libgdx.graphics.GeometricObject;
import de.julianfeja.games.libgdx.graphics.Rectangle;
import de.julianfeja.games.libgdx.graphics.texture.TextureObject;

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

	public static FixtureDef getDefaultFixtureDef(float density,
			short groupIndex) {
		FixtureDef fixtureDef = new FixtureDef();

		fixtureDef.filter.groupIndex = groupIndex; // 0 => no collision
		// // group,negative =>
		// // never collide, positive => always
		// // collide
		// fixtureDef.filter.categoryBits = 0x0001;
		// fixtureDef.filter.maskBits = 0x0001;
		// fixtureDef.isSensor = false;
		fixtureDef.density = density;
		fixtureDef.friction = 0.5f;
		fixtureDef.restitution = 0.2f;

		return fixtureDef;
	}

	@Override
	public void createFixtures(Body body) {

		for (PolygonShape bodyPoly : textureObject
				.createPolygonShapes(dimension)) {

			FixtureDef fixtureDef = getDefaultFixtureDef(
					textureObject.getDensity(), textureObject.getGroupIndex());

			fixtureDef.shape = bodyPoly;

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
