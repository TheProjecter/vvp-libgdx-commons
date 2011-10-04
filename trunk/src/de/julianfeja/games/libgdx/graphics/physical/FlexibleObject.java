package de.julianfeja.games.libgdx.graphics.physical;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.DistanceJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.utils.Array;

import de.julianfeja.games.libgdx.graphics.defs.BodyDefinition;
import de.julianfeja.games.libgdx.graphics.defs.BoneDefinition;
import de.julianfeja.games.libgdx.graphics.defs.BoneDefinition.Direction;
import de.julianfeja.games.libgdx.graphics.defs.JointDefinition;
import de.julianfeja.games.libgdx.graphics.texture.TextureObject;

public class FlexibleObject extends PhysicsObjectGroup {

	public FlexibleObject(Vector2 position, Vector2 scale,
			BodyDefinition bodyDef, TextureObject textureObject, World world) {
		super(position, scale, cutBody(textureObject, bodyDef),
				createJoints(bodyDef), textureObject, world);
		// TODO Auto-generated constructor stub
	}

	protected static Array<JointDefinition> createJoints(
			BodyDefinition bodyDefinition) {
		Array<JointDefinition> ret = new Array<JointDefinition>();

		Array<Vector2> points = bodyDefinition.getBoneDefinition().getPoints();

		RevoluteJointDef revoluteJointDef = new RevoluteJointDef();

		revoluteJointDef.enableMotor = false;
		revoluteJointDef.motorSpeed = 0f;
		revoluteJointDef.maxMotorTorque = 20f;
		revoluteJointDef.collideConnected = false;

		DistanceJointDef distanceJointDef = new DistanceJointDef();

		for (int i = 1; i < points.size - 1; i++) {
			String idBodyA = bodyDefinition.getId() + "#" + (i - 1);
			String idBodyB = bodyDefinition.getId() + "#" + (i);
			ret.add(new JointDefinition("", idBodyA, idBodyB, points.get(i),
					points.get(i), revoluteJointDef));

			ret.add(new JointDefinition("", idBodyA, idBodyB, points.get(i),
					points.get(i), distanceJointDef).setCenterToCenter(true));
		}
		return ret;

	}

	protected static Map<String, BodyDefinition> cutBody(
			TextureObject textureObject, BodyDefinition bodyDefinition) {
		Map<String, BodyDefinition> ret = new LinkedHashMap<String, BodyDefinition>();

		BoneDefinition boneDef = bodyDefinition.getBoneDefinition();
		Array<Array<Vector2>> cutLines = createCutLines(boneDef, textureObject);

		Array<TextureObject> elements = textureObject.cut(cutLines);

		for (int i = 0; i < elements.size; i++) {
			ret.put(bodyDefinition.getId() + "#" + i,
					new BodyDefinition(bodyDefinition.getId(), elements.get(i)
							.getOutline(), bodyDefinition.getDensity(), null,
							bodyDefinition.getCollideGroup())
							.setFixedRotation(bodyDefinition.getFixedRotation()));
		}

		return ret;
	}

	public static Array<Array<Vector2>> createCutLines(BoneDefinition boneDef,
			TextureObject textureObject) {
		Array<Array<Vector2>> lines = new Array<Array<Vector2>>();

		for (int i = 1; i < boneDef.getPoints().size - 1; i++) {
			Array<Vector2> line = new Array<Vector2>(2);
			Vector2 point = boneDef.getPoints().get(i);

			if (boneDef.getDirection() == Direction.Horizontal) {
				line.add(new Vector2(point.x, 0));
				line.add(new Vector2(point.x, textureObject.getTexture()
						.getHeight()));
			} else if (boneDef.getDirection() == Direction.Vertical) {
				line.add(new Vector2(0, point.y));
				line.add(new Vector2(textureObject.getTexture().getWidth(),
						point.y));
			}

			lines.add(line);
		}

		return lines;
	}

}