package de.julianfeja.games.libgdx.control;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import de.julianfeja.games.libgdx.graphics.Paintable;
import de.julianfeja.games.libgdx.graphics.physical.PhysicsObject;

public class Handle implements Paintable {
	protected PhysicsObject physicsObject;

	public Handle(PhysicsObject physicsObject) {
		this.physicsObject = physicsObject;
	}

	public PhysicsObject getPhysicsObject() {
		return this.physicsObject;
	}

	@Override
	public void paint(Camera camera, SpriteBatch batch) {
		// TODO Auto-generated method stub

	}

	public Vector2 getScale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
}
