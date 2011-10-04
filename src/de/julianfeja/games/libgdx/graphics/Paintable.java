package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface Paintable {
	public void paint(Camera camera, SpriteBatch batch);

	public void dispose();
}
