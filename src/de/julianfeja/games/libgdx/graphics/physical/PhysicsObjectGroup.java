package de.julianfeja.games.libgdx.graphics.physical;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.julianfeja.games.libgdx.graphics.defs.BodyDefinition;
import de.julianfeja.games.libgdx.graphics.defs.JointDefinition;
import de.julianfeja.games.libgdx.graphics.texture.SimpleOutlinedTextureObject;
import de.julianfeja.games.libgdx.graphics.texture.TextureObject;

public class PhysicsObjectGroup extends PhysicsObject {
	protected Map<String, Joint> joints = new LinkedHashMap<String, Joint>();
	protected Map<String, PhysicsObject> physicsObjects = new LinkedHashMap<String, PhysicsObject>();

	protected TextureObject textureObject;

	public PhysicsObjectGroup(Vector2 position, Vector2 scale,
			Map<String, BodyDefinition> bodyDefs,
			Array<JointDefinition> jointDefs, TextureObject textureObject,
			World world) {
		super(position, scale, new Vector2(), world);

		this.textureObject = textureObject;

		for (JointDefinition jointDef : jointDefs) {
			addJoint(jointDef, bodyDefs);
		}

		body = physicsObjects.get(physicsObjects.keySet().iterator().next())
				.getBody();
	}

	public PhysicsObjectGroup(PhysicsObjectGroup other) {
		super(other.getPosition(), other.getScale(), other.dimension,
				other.world);

		this.textureObject = other.textureObject;
		this.joints = other.joints;
		this.body = other.body;
		this.physicsObjects = other.physicsObjects;
	}

	protected void addJoint(JointDefinition jointDef,
			Map<String, BodyDefinition> bodyDefs) {
		String idBody1 = jointDef.getIdBody1();
		String idBody2 = jointDef.getIdBody2();

		Vector2 pos1 = null;
		Vector2 pos2 = null;
		Vector2 center = null;

		PhysicsObject physicsObject1 = null;
		PhysicsObject physicsObject2 = null;

		if (physicsObjects.containsKey(idBody1)) {
			physicsObject1 = physicsObjects.get(idBody1);
			pos1 = physicsObject1.getPosition();
		}

		if (physicsObjects.containsKey(idBody2)) {
			physicsObject2 = physicsObjects.get(idBody2);
			pos2 = physicsObject2.getPosition();
		}

		if (pos1 == null && pos2 == null) {
			pos1 = position.cpy();
		} else if (pos1 == null) {
			pos1 = jointDef.getPoint1(this.scale)
					.sub(jointDef.getPoint2(this.scale))
					.add(new Vector2(-pos2.x, pos2.y));
			pos1.x *= -1;
		}

		if (pos2 == null) {
			pos2 = jointDef.getPoint2(this.scale)
					.sub(jointDef.getPoint1(this.scale))
					.add(new Vector2(-pos1.x, pos1.y));
			pos2.x *= -1;
		}

		if (physicsObject1 == null) {
			physicsObject1 = addBody(idBody1, bodyDefs.get(idBody1), pos1);
		}
		if (physicsObject2 == null) {
			physicsObject2 = addBody(idBody2, bodyDefs.get(idBody2), pos2);
		}

		center = jointDef.getPoint2(this.scale);
		center.y *= -1;
		center.add(pos2);

		jointDef.initialize(physicsObject1.getBody(), physicsObject2.getBody(),
				center);

		jointDef.getJointDef().collideConnected = false;

		world.createJoint(jointDef.getJointDef());
	}

	protected PhysicsObject addBody(String key, BodyDefinition bodyDef,
			Vector2 pos) {

		PhysicsObject physicsObject = null;

		if (bodyDef.getBoneDefinition() != null) {
			physicsObject = new FlexibleObject(pos, this.scale, bodyDef,
					new SimpleOutlinedTextureObject(textureObject,
							bodyDef.getOutline()), world);
		} else {
			physicsObject = new TexturedMeshPhysicsObject(pos, this.scale,
					new SimpleOutlinedTextureObject(textureObject, bodyDef
							.getOutline()).setDensity(bodyDef.getDensity())
							.setGroupIndex(bodyDef.getCollideGroup())
							.setFixedRotation(bodyDef.getFixedRotation()),
					world);

		}
		physicsObjects.put(key, physicsObject);

		return physicsObject;
	}

	@Override
	public void paint(Camera camera, SpriteBatch batch) {
		for (String key : physicsObjects.keySet()) {
			physicsObjects.get(key).paint(camera, batch);
		}
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setActive(boolean a) {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

}
