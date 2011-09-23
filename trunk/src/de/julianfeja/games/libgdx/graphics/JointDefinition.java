package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.JointDef.JointType;

public class JointDefinition {
	protected String id;

	protected String idBody1;
	protected String idBody2;

	protected Vector2 point1;
	protected Vector2 point2;

	protected JointType type;

	protected Vector2 limits = null;

	public JointDefinition(String id, String idBody1, String idBody2,
			Vector2 point1, Vector2 point2, JointType type) {
		this.id = id;

		this.idBody1 = idBody1;
		this.idBody2 = idBody2;

		this.point1 = point1.cpy().mul(1 / GeometricObject.PPM);
		this.point2 = point2.cpy().mul(1 / GeometricObject.PPM);
		// this.point1.y *= -1;
		// this.point2.y *= -1;

		this.type = type;
	}

	public JointDefinition(String id, String idBody1, String idBody2,
			Vector2 point1, Vector2 point2, String jType) {
		this(id, idBody1, idBody2, point1, point2, JointType.valueOf(jType));
	}

	public String getId() {
		return this.id;
	}

	public String getIdBody1() {
		return this.idBody1;
	}

	public String getIdBody2() {
		return this.idBody2;
	}

	public Vector2 getPoint1() {
		return this.point1.cpy();
	}

	public Vector2 getPoint2() {
		return this.point2.cpy();
	}

	public JointType getType() {
		return this.type;
	}

	public Vector2 getLimits() {
		return this.limits;
	}

	public void setLimits(Vector2 limits) {
		this.limits = limits;

	}
}
