package de.julianfeja.games.libgdx.graphics.physical;

import java.util.Map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import de.julianfeja.games.libgdx.graphics.defs.BodyDefinition;
import de.julianfeja.games.libgdx.graphics.defs.JointDefinition;
import de.julianfeja.games.libgdx.graphics.texture.TextureObject;

public class FlexibleObject extends PhysicsObjectGroup {

	public FlexibleObject(Vector2 position, Vector2 scale,
			Map<String, BodyDefinition> bodyDefs,
			Array<JointDefinition> jointDefs, TextureObject textureObject,
			World world) {
		super(position, scale, bodyDefs, jointDefs, textureObject, world);
		// TODO Auto-generated constructor stub
	}

}