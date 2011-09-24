package de.julianfeja.games.libgdx.graphics.defs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BodyDefinition {
	protected String id;
	protected Array<Vector2> outline;
	protected float density;

	public BodyDefinition(String id, Array<Vector2> outline, float density) {
		this.id = id;
		this.outline = outline;
		this.density = density;
	}

	public String getId() {
		return this.id;
	}

	public Array<Vector2> getOutline() {
		return this.outline;
	}

	public float getDensity() {
		return this.density;
	}
}
