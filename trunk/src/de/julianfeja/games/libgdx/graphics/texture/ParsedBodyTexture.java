package de.julianfeja.games.libgdx.graphics.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import daniel.weck.BayazitDecomposer;
import daniel.weck.TextureConverter;
import de.julianfeja.games.libgdx.graphics.Rectangle;

public class ParsedBodyTexture extends TextureObject {

	public ParsedBodyTexture(Pixmap pixmap, Rectangle rect, String assetPath) {
		super(pixmap, rect, assetPath);

		init(createOutline(pixmap, rect));
	}

	public ParsedBodyTexture(Pixmap pixmap, String assetPath) {
		this(pixmap,
				new Rectangle(0, 0, pixmap.getWidth(), pixmap.getHeight()),
				assetPath);
	}

	protected Array<Vector2> createOutline(Pixmap pixmap, Rectangle rect) {
		int size = rect.width * rect.height;
		int[] array = new int[size];
		for (int y = rect.y; y < rect.height; y++) {
			for (int x = rect.x; x < rect.width; x++) {
				int color = pixmap.getPixel(x, y);
				array[x + y * rect.width] = color;
			}
		}
		int w = rect.width;
		int h = rect.height;
		Array<Vector2> outline = null;
		try {
			outline = TextureConverter.createPolygon(array, w, h);
		} catch (Exception e) {
			Gdx.app.log(this.getClass().getName(), e.getMessage());
		}

		if (!BayazitDecomposer.IsCounterClockWise(outline)) {
			outline.reverse();
		}

		return outline;
	}

	protected Array<Vector2> createOutline(Pixmap pixmap) {
		return createOutline(pixmap, new Rectangle(0, 0, pixmap.getWidth(),
				pixmap.getHeight()));
	}

}
