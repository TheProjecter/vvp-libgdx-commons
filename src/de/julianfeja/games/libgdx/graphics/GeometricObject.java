package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.math.Vector2;

public abstract class GeometricObject implements Paintable {
	protected Vector2 position;
	protected Vector2 scale;

	/*
	 * pixels per meter
	 */
	public final static float PPM = 15f;

	public GeometricObject(Vector2 position, Vector2 scale) {
		this.position = position;
		this.scale = scale;

	}

	@Override
	public Vector2 getScale() {
		return scale;
	}
}
