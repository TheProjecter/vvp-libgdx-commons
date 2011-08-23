package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.physics.box2d.Body;

public interface Physical {
	public Body createBody();

	public void createFixtures(Body body);

	public boolean isActive();

	public void setActive(boolean a);

	public void update();
}
