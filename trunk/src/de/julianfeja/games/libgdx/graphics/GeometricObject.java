package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.math.Vector2;

public abstract class GeometricObject implements Paintable {
	protected Vector2 position;
	protected Vector2 dimension;

	/*
	 * pixels per meter
	 */
	public final static int PPM = 15;

	public GeometricObject(Vector2 position, Vector2 dimension) {
		this.position = position;
		this.dimension = dimension;

	}

	@Override
	public Vector2 getPosition() {
		return position;
	}
}
