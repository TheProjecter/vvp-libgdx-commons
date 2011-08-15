package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

abstract public class PhysicsObject extends GeometricObject implements Physical {
	protected Body body;
	protected World world;

	public PhysicsObject(Vector2 position, Vector2 dimension, World world) {
		super(position, dimension);

		this.world = world;
	}

	@Override
	public void createFixtures(Body body) {
		PolygonShape boxPoly = new PolygonShape();
		boxPoly.setAsBox(dimension.x / 2, dimension.y / 2);

		body.createFixture(boxPoly, 1);

		boxPoly.dispose();

	}

	@Override
	public Body createBody() {
		BodyDef boxBodyDef = new BodyDef();
		boxBodyDef.type = BodyType.DynamicBody;
		boxBodyDef.position.x = position.x;
		boxBodyDef.position.y = position.y;
		Body body = world.createBody(boxBodyDef);

		createFixtures(body);

		return body;
	}

	@Override
	public void paint(SpriteBatch batch) {
		position = body.getPosition();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		dispose();
	}

	@Override
	public void dispose() {
		world.destroyBody(body);
	}

}
