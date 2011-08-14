package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public interface Paintable {
	public void paint(SpriteBatch batch);

	public Vector2 getPosition();

	public void dispose();
}
