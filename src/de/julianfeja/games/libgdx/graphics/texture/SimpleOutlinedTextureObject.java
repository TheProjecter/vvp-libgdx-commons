package de.julianfeja.games.libgdx.graphics.texture;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import de.julianfeja.games.libgdx.graphics.Rectangle;

public class SimpleOutlinedTextureObject extends TextureObject {

	public SimpleOutlinedTextureObject(Pixmap pixmap, Rectangle rect,
			Array<Vector2> outline, String assetPath) {
		super(pixmap, rect, assetPath);

		init(outline);
	}

	public SimpleOutlinedTextureObject(Pixmap pixmap, Array<Vector2> outline,
			String assetPath) {
		this(pixmap,
				new Rectangle(0, 0, pixmap.getWidth(), pixmap.getHeight()),
				outline, assetPath);
	}

	public SimpleOutlinedTextureObject(TextureObject other,
			Array<Vector2> outline) {
		super(other, outline);
		// TODO Auto-generated constructor stub
	}

}