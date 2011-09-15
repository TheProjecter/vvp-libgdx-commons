package de.julianfeja.games.libgdx.graphics.parallaxbackground;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import de.julianfeja.games.libgdx.graphics.Paintable;
import de.julianfeja.games.libgdx.graphics.camera.OrthoCamera;

public class ParallaxBackground implements Paintable {
	protected OrthoCamera camera;

	protected ParallaxBackgroundLayer[] layers;

	public ParallaxBackground(OrthoCamera camera,
			ParallaxBackgroundLayer[] layers) {
		this.camera = camera;

		this.layers = layers;
	}

	@Override
	public void paint(SpriteBatch batch) {

		for (ParallaxBackgroundLayer layer : layers) {

			Rectangle drawRegion = new Rectangle(layer.getDrawRegion());

			if (drawRegion.width == -1) {
				drawRegion.width = (camera.getBorders().width - camera.viewportWidth)
						* (1 - layer.parallaxValues.x) + camera.viewportWidth;
				drawRegion.x = -drawRegion.width / 2;
			}
			if (drawRegion.height == -1) {
				drawRegion.height = (camera.getBorders().height - camera.viewportHeight)
						* (1 - layer.parallaxValues.y) + camera.viewportHeight;
				drawRegion.y = -drawRegion.height / 2;
			}

			batch.setProjectionMatrix(camera.calculateParallaxMatrix(
					layer.getParallaxValues().x, layer.getParallaxValues().y));

			batch.begin();
			batch.draw(layer.textureRegion, drawRegion.x, drawRegion.y,
					drawRegion.width, drawRegion.height);
			batch.end();
		}
	}

	@Override
	public Vector2 getScale() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void dispose() {
	}
}
