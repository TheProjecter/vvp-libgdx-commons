package de.julianfeja.games.libgdx.graphics.physical;

import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;

public class FlexiblePhysicsObjectGroup extends PhysicsObjectGroup {

	public FlexiblePhysicsObjectGroup(PhysicsObjectGroup other) {
		super(other);

		for (Joint joint : this.joints) {
			DistanceJointDef distanceJointDef = new DistanceJointDef();
			distanceJointDef.dampingRatio = 1f;
			distanceJointDef.frequencyHz = 10;

			distanceJointDef.initialize(joint.getBodyA(), joint.getBodyB(),
					joint.getBodyA().getWorldCenter(), joint.getBodyA()
							.getWorldCenter());

			world.createJoint(distanceJointDef);
		}
	}
}
