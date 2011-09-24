package de.julianfeja.games.libgdx.graphics.texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.julianfeja.games.libgdx.graphics.Rectangle;

public class BoxBodyTexture extends TextureObject {

	public BoxBodyTexture(Pixmap pixmap, Rectangle rect) {
		super(pixmap, rect);

		Array<Vector2> outline = new Array<Vector2>();

		outline.add(new Vector2(rect.x, rect.y));
		outline.add(new Vector2(rect.x, rect.y + rect.height));
		outline.add(new Vector2(rect.x + rect.width, rect.y + rect.height));
		outline.add(new Vector2(rect.x + rect.width, rect.y));

		// if (!BayazitDecomposer.IsCounterClockWise(outline)) {
		// outline.reverse();
		// }

		init(outline);
	}

	public BoxBodyTexture(Pixmap pixmap) {
		this(pixmap, new Rectangle(0, 0, pixmap.getWidth(), pixmap.getHeight()));
	}
}
