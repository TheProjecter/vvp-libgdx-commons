package de.julianfeja.games.libgdx.graphics.defs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BodyDefinition {
	protected String id;
	protected Array<Vector2> outline;
	protected float density;
	protected BoneDefinition boneDefinition;

	public BodyDefinition(String id, Array<Vector2> outline, float density,
			BoneDefinition boneDefinition) {
		this.id = id;
		this.outline = outline;
		this.density = density;
		this.boneDefinition = boneDefinition;
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

	public BoneDefinition getBoneDefinition() {
		return this.boneDefinition;
	}

	public boolean isBonedBody() {
		if (boneDefinition == null) {
			return false;
		} else
			return true;
	}
}
