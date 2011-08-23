package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class TexturedMeshPhysicsObject extends SpritePhysicsObject {

	public TexturedMeshPhysicsObject(Vector2 position, Vector2 dimension,
			TextureObject textureObject, World world) {
		super(position, dimension, textureObject, world);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void paint(SpriteBatch batch) {
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

}
