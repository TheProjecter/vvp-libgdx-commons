package de.julianfeja.games.libgdx.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;

import daniel.weck.BayazitDecomposer;

public class ZipMultitextureInput extends ZipInput {
	protected Pixmap pixmap;
	protected Array<Array<Vector2>> outlines;

	public ZipMultitextureInput(String assetPath) {
		super(assetPath);

		try {
			byte[] textureByteArray = getEntryAsByteArray("texture.png");

			pixmap = new Pixmap(textureByteArray, 0, textureByteArray.length);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		SAXReader reader = new SAXReader();

		try {
			InputStream is = getEntryStream("def.xml");

			Document document = reader.read(is);

			outlines = parseOutlines(document);

			is.close();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected Array<Array<Vector2>> parseOutlines(Document document) {
		Array<Array<Vector2>> outlines = new Array<Array<Vector2>>();

		List bodys = document.selectNodes("//body");

		for (Object bodyObject : bodys) {
			Node bodyNode = (Node) bodyObject;

			Array<Vector2> outline = new Array<Vector2>();

			List points = bodyNode.selectNodes("./point");

			for (Object pointObject : points) {
				Node pointNode = (Node) pointObject;

				float x = Float.parseFloat(pointNode.valueOf("@x"));
				float y = Float.parseFloat(pointNode.valueOf("@y"));

				outline.add(new Vector2(x, y + pixmap.getHeight() / 2));
			}

			if (!BayazitDecomposer.IsCounterClockWise(outline)) {
				outline.reverse();
			}

			outlines.add(outline);
		}

		return outlines;
	}

	public Pixmap getPixmap() {
		return pixmap;
	}

	public Array<Vector2> getOutline(int index) {
		if (index >= 0 && index < outlines.size) {
			return outlines.get(index);
		} else {
			return null;
		}

	}
}
