package de.julianfeja.games.libgdx.graphics.defs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BodyDefinition {
	protected String id;
	protected Array<Vector2> outline;
	protected float density;
	protected BoneDefinition boneDefinition;
	protected boolean staticBody = false;
	protected boolean fixedRotation = false;
	protected short collideGroup;

	public BodyDefinition(String id, Array<Vector2> outline, float density,
			BoneDefinition boneDefinition, short collideGroup) {
		this.id = id;
		this.outline = outline;
		this.density = density;
		this.boneDefinition = boneDefinition;
		this.collideGroup = collideGroup;
	}

	public short getCollideGroup() {
		return this.collideGroup;
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

	public boolean getStaticBody() {
		return staticBody;
	}

	public boolean getFixedRotation() {
		return fixedRotation;
	}

	public BodyDefinition setStaticBody(boolean staticBody) {
		this.staticBody = staticBody;
		return this;
	}

	public BodyDefinition setFixedRotation(boolean fixedRotation) {
		this.fixedRotation = fixedRotation;
		return this;
	}

}
