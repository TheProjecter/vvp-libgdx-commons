package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class TexturedMeshPhysicsObject extends SpritePhysicsObject {

	public TexturedMeshPhysicsObject(Vector2 position, Vector2 dimension,
			TextureObject textureObject, World world, Vector2 velocity,
			float angle) {
		super(position, dimension, textureObject, world);
		// TODO Auto-generated constructor stub

		if (velocity != null) {
			body.setLinearVelocity(velocity);
		}

		body.setTransform(body.getPosition(), angle);

	}

	public TexturedMeshPhysicsObject(Vector2 position, Vector2 dimension,
			TextureObject textureObject, World world) {
		this(position, dimension, textureObject, world, null, 0);

	}

	public TexturedMeshPhysicsObject(Vector2 position,
			TextureObject textureObject, World world) {
		this(position, textureObject.getDimension(), textureObject, world,
				null, 0);

	}

	public TexturedMeshPhysicsObject(Vector2 position, float scale,
			TextureObject textureObject, World world) {
		this(position, textureObject.getDimension().mul(scale), textureObject,
				world, null, 0);

	}

	@Override
	public void paint(SpriteBatch batch) {
		update();

		GL10 gl = Gdx.graphics.getGL10();
		gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		gl.glEnable(GL10.GL_BLEND);
		gl.glEnable(GL10.GL_TEXTURE_2D);

		textureObject.getTexture().bind();

		gl.glPushMatrix();

		gl.glTranslatef(body.getPosition().x, body.getPosition().y, 0);

		gl.glRotatef(body.getAngle() * MathUtils.radiansToDegrees, 0, 0, 1);
		gl.glScalef(dimension.x / textureObject.texture.getWidth(),
				-dimension.y / textureObject.texture.getHeight(), 1f);

		Mesh mesh = textureObject.getMesh();

		mesh.render(GL10.GL_TRIANGLE_FAN);

		gl.glPopMatrix();
	}

	public Array<TexturedMeshPhysicsObject> disassemble() {
		Array<TexturedMeshPhysicsObject> subParts = new Array<TexturedMeshPhysicsObject>();

		Array<Array<Vector2>> polygons = textureObject.getPolygons();

		if (polygons.size > 1) {

			for (Array<Vector2> polygon : polygons) {
				polygon.reverse();

				Vector2 min = new Vector2(Integer.MAX_VALUE, Integer.MAX_VALUE);
				Vector2 max = new Vector2(0, 0);

				for (Vector2 v : polygon) {
					if (v.x < min.x) {
						min.x = v.x;
					}
					if (v.y < min.y) {
						min.y = v.y;
					}
					if (v.x > max.x) {
						max.x = v.x;
					}
					if (v.y > max.y) {
						max.y = v.y;
					}
				}

				subParts.add(new TexturedMeshPhysicsObject(body.getPosition(),
						dimension, textureObject.getCopy(polygon), world, body
								.getLinearVelocity(), body.getAngle()));

				polygon.reverse();
			}
		}

		return subParts;

	}

}
