package de.julianfeja.games.libgdx.graphics.defs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

public class BoneDefinition {
	public enum Direction {
		Horizontal, Vertical
	}

	protected Direction direction;
	protected Array<Vector2> points;

	public BoneDefinition(Direction direction, Array<Vector2> points) {
		this.direction = direction;
		this.points = points;
	}

	public Direction getDirection() {
		return this.direction;
	}

	public Array<Vector2> getPoints() {
		return this.points;
	}

}
