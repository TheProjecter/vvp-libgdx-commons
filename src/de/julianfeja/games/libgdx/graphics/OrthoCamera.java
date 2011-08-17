package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class OrthoCamera extends OrthographicCamera {
	protected Rectangle borders;

	protected Vector3 lastValidPosition;

	protected Matrix4 parallaxView = new Matrix4();
	protected Matrix4 parallaxCombined = new Matrix4();
	protected Vector3 tmp = new Vector3();
	protected Vector3 tmp2 = new Vector3();

	public OrthoCamera(float viewportWidth, float viewportHeight) {
		super(viewportWidth, viewportHeight);

		borders = new Rectangle(-38f, -40f, 76f, 80f);
		lastValidPosition = position.cpy();

	}

	public Matrix4 calculateParallaxMatrix(float parallaxX, float parallaxY) {
		update();
		tmp.set(position);
		tmp.x *= parallaxX;
		tmp.y *= parallaxY;

		parallaxView.setToLookAt(tmp, tmp2.set(tmp).add(direction), up);
		parallaxCombined.set(projection);
		Matrix4.mul(parallaxCombined.val, parallaxView.val);
		return parallaxCombined;
	}

	@Override
	public void translate(float x, float y, float z) {
		// TODO Auto-generated method stub
		super.translate(x, 0, z);

		if (position.x - viewportWidth * zoom / 2 < borders.x || position.x + viewportWidth * zoom / 2 > borders.x + borders.width) {
			position.x = lastValidPosition.x;
		} else {
			lastValidPosition.x = position.x;
		}

		// if (position.y - viewportHeight * zoom / 2 < borders.y || position.y
		// + viewportWidth * zoom / 2 > borders.y + borders.height) {
		// position.y = lastValidPosition.y;
		// } else {
		// lastValidPosition.y = position.y;
		// }
	}

	public boolean zoomAndMove(int amount) {
		Float newZoom = zoom + 0.1f * amount;

		if (newZoom < 1f) {
			return false;
		}

		if (amount > 0) {
			if (position.x - viewportWidth * newZoom / 2 < borders.x) {
				if (viewportWidth * newZoom < borders.width) {
					position.x = borders.x + viewportWidth * newZoom / 2;
				} else {
					return false;
				}
			}

			if (position.x + viewportWidth * newZoom / 2 > borders.x + borders.width) {
				if (viewportWidth * newZoom < borders.width) {
					position.x = borders.x + borders.width - viewportWidth * newZoom / 2;
				} else {
					return false;
				}
			}

			if (position.y - viewportHeight * newZoom / 2 < borders.y) {
				if (viewportHeight * newZoom < borders.height) {
					position.y = borders.y + viewportHeight * newZoom / 2;
				} else {
					return false;
				}
			}

			if (position.y + viewportHeight * newZoom / 2 > borders.y + borders.height) {
				if (viewportHeight * newZoom < borders.height) {
					position.y = borders.y + borders.height - viewportHeight * newZoom / 2;
				} else {
					return false;
				}
			}

		}

		zoom = newZoom;

		return true;

	}

	public Rectangle getBorders() {
		return borders;
	}
}
