package de.julianfeja.games.libgdx.graphics;

import java.util.Iterator;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

abstract public class PhysicsObject extends GeometricObject implements Physical {
	protected Body body;
	protected World world;
	protected boolean active = false;
	protected Vector2 dimension;

	public PhysicsObject(Vector2 position, Vector2 scale, Vector2 dimension,
			World world) {
		super(position, scale);

		this.dimension = dimension;
		this.world = world;
		active = true;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean a) {
		active = a;

		body.setActive(active);
	}

	public Body getBody() {
		return this.body;
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
		update();
	}

	@Override
	public void update() {
		position = body.getPosition();
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();

		dispose();
	}

	@Override
	public void dispose() {
		body.setActive(false);
		body.setAwake(false);

		Iterator<Joint> joints = world.getJoints();

		while (joints.hasNext()) {
			Joint joint = joints.next();

			if (joint.getBodyA() == body || joint.getBodyB() == body) {
				world.destroyJoint(joint);
			}
		}

		world.destroyBody(body);
		body = null;

	}

}
