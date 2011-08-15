package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.physics.box2d.Body;

public interface Physical {
	public Body createBody();

	public void createFixtures(Body body);
}
