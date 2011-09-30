package de.julianfeja.games.libgdx.graphics.defs;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.JointDef;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;

import de.julianfeja.games.libgdx.graphics.GeometricObject;

public class JointDefinition {
	protected String id;

	protected String idBody1;
	protected String idBody2;

	protected Vector2 point1;
	protected Vector2 point2;

	protected Vector2 axis = new Vector2(0f, 1f);
	protected boolean centerToCenter = false;

	protected JointDef jointDef;

	public JointDefinition(String id, String idBody1, String idBody2,
			Vector2 point1, Vector2 point2, JointDef jointDef) {
		this.id = id;

		this.idBody1 = idBody1;
		this.idBody2 = idBody2;

		this.point1 = point1.cpy().mul(1 / GeometricObject.PPM);
		this.point2 = point2.cpy().mul(1 / GeometricObject.PPM);

		this.jointDef = jointDef;
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

	public Vector2 getPoint1(Vector2 scale) {
		return new Vector2(point1.x * scale.x, point1.y * scale.y);
	}

	public Vector2 getPoint2(Vector2 scale) {
		return new Vector2(point2.x * scale.x, point2.y * scale.y);
	}

	public JointDefinition setAxis(Vector2 axis) {
		this.axis.set(axis);

		return this;
	}

	public JointDefinition setCenterToCenter(boolean centerToCenter) {
		this.centerToCenter = centerToCenter;

		return this;
	}

	// public JointDefinition setLimits(float lowerAngle, float upperAngle) {
	// if (type == JointType.RevoluteJoint) {
	// RevoluteJointDef def = (RevoluteJointDef) jointDef;
	//
	// def.enableLimit = true;
	// def.lowerAngle = lowerAngle;
	// def.upperAngle = upperAngle;
	// }
	//
	// return this;
	// }
	//
	// public JointDefinition setMotor(float motorSpeed, float maxMotorTorque) {
	// if (type == JointType.RevoluteJoint) {
	// RevoluteJointDef def = (RevoluteJointDef) jointDef;
	//
	// def.enableMotor = true;
	// def.motorSpeed = motorSpeed;
	// def.maxMotorTorque = maxMotorTorque;
	// }
	//
	// return this;
	// }
	//
	// public JointDefinition setCollideConnected(boolean collide) {
	// jointDef.collideConnected = collide;
	//
	// return this;
	// }
	//
	// public JointDefinition setDistance(float distance) {
	// if (type == JointType.DistanceJoint) {
	// DistanceJointDef def = (DistanceJointDef) jointDef;
	//
	// def.length = 20.0f;
	// }
	//
	// return this;
	// }

	public void initialize(Body bodyA, Body bodyB, Vector2 center) {
		switch (jointDef.type) {
		case RevoluteJoint:
			RevoluteJointDef revDef = (RevoluteJointDef) jointDef;

			revDef.initialize(bodyA, bodyB, center);
			break;

		case DistanceJoint:
			DistanceJointDef disDef = (DistanceJointDef) jointDef;

			if (centerToCenter) {
				disDef.initialize(bodyA, bodyB, bodyA.getWorldCenter(),
						bodyB.getWorldCenter());
			} else {
				disDef.initialize(bodyA, bodyB, center, center);
			}

			break;

		case PrismaticJoint:
			PrismaticJointDef prisDef = (PrismaticJointDef) jointDef;

			prisDef.initialize(bodyA, bodyB, center, new Vector2(0f, 1f));

		default:
			break;
		}
	}

	public JointDef getJointDef() {
		return this.jointDef;
	}

}
