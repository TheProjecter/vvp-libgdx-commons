package de.julianfeja.games.libgdx.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import daniel.weck.BayazitDecomposer;
import daniel.weck.TextureConverter;

public class TextureObject {
	protected String assetPath;
	protected Texture texture;
	protected Array<Array<Vector2>> polygons;
	protected Mesh mesh;

	public TextureObject(String assetPath) {
		final Pixmap pixmap = new Pixmap(Gdx.files.internal(assetPath));

		texture = new Texture(pixmap);

		Array<Vector2> outline = createOutline(pixmap);

		polygons = createPolygons(outline);

		mesh = create_TextureMesh(outline);

		pixmap.dispose();
	}

	public Texture getTexture() {
		return texture;
	}

	public String getAssetPath() {
		return assetPath;
	}

	public Array<Array<Vector2>> getPolygons() {
		return polygons;
	}

	public Mesh getMesh() {
		return mesh;
	}

	protected Array<Vector2> createOutline(Pixmap pixmap) {
		int size = pixmap.getWidth() * pixmap.getHeight();
		int[] array = new int[size];
		for (int y = 0; y < pixmap.getHeight(); y++) {
			for (int x = 0; x < pixmap.getWidth(); x++) {
				int color = pixmap.getPixel(x, y);
				array[x + y * pixmap.getWidth()] = color;
			}
		}
		int w = pixmap.getWidth();
		int h = pixmap.getHeight();
		Array<Vector2> outline = null;
		try {
			outline = TextureConverter.createPolygon(array, w, h);
		} catch (Exception e) {
			Gdx.app.log(this.getClass().getCanonicalName(), e.getMessage());
		}

		if (!BayazitDecomposer.IsCounterClockWise(outline)) {
			outline.reverse();
		}

		return outline;
	}

	public Array<Array<Vector2>> createPolygons(Array<Vector2> outline) {

		Array<Array<Vector2>> polygons = BayazitDecomposer
				.ConvexPartition(outline);

		for (Array<Vector2> polygon : polygons) {
			if (BayazitDecomposer.IsCounterClockWise(polygon)) {
				Gdx.app.log(
						"DEBUG",
						"Counter Clockwise body polygon vertices (after BayazitDecomposer.ConvexPartition), reversing... ["
								+ assetPath + "] ");
				polygon.reverse();
			}
		}
		return polygons;

	}

	public Array<PolygonShape> createPolygonShapes(Vector2 dimension) {
		Vector2 bodySize = new Vector2(dimension.x / texture.getWidth(),
				dimension.y / texture.getHeight());

		Array<PolygonShape> polygonShapes;

		polygonShapes = new Array<PolygonShape>(polygons.size);
		for (int j = 0; j < polygons.size; j++) {
			Array<Vector2> vertices_original = polygons.get(j);
			// ClockWise requirement should have been dealt with at
			// create_sprite() stage !
			if (BayazitDecomposer.IsCounterClockWise(vertices_original)) {
				Gdx.app.log(this.getClass().getName(),
						"Counter Clockwise body polygon vertices (ORIGINAL) for Box2D ?! WTF ?");
			}
			Vector2[] vertices_adjusted = new Vector2[vertices_original.size];
			for (int t = 0; t < vertices_original.size; t++) {
				Vector2 v = vertices_original.get(t).cpy();
				v.x *= bodySize.x;
				v.y *= bodySize.y;
				v.x -= (bodySize.x / 2);
				v.y = (bodySize.y / 2) - v.y;
				vertices_adjusted[t] = v;
			}
			if (!BayazitDecomposer.IsCounterClockWise(vertices_adjusted)) {
				Gdx.app.log(this.getClass().getName(),
						"Clockwise body polygon vertices (ADJUSTED) for Box2D ?! WTF");
			}

			PolygonShape polygonShape = new PolygonShape();
			polygonShape.set(vertices_adjusted);
			polygonShapes.add(polygonShape);

		}

		return polygonShapes;
	}

	Mesh create_TextureMesh(Array<Vector2> outline) {
		int stride = 5;
		float[] vertices = new float[outline.size * stride];
		short[] indices = new short[outline.size];

		int j = -1;
		int offset = -1;
		for (int i = 0; i < outline.size; i++) {
			Vector2 vect = outline.get(i);
			j = i * stride;
			offset = -1;
			++offset;
			vertices[j + offset] = vect.x;
			++offset;
			vertices[j + offset] = vect.y;
			++offset;
			vertices[j + offset] = 0;
			//
			// ++offset;
			// vertices[j + offset] = color; // Color.toFloatBits(0, 255, 0,
			// 255);
			//
			float u = (vect.x) / texture.getWidth();
			float v = (vect.y) / texture.getHeight();// (getTextureRegion().getHeight()
														// / 2 - vect.y /
														// ratio) /
														// textureRegion.getHeight();

			++offset;
			vertices[j + offset] = u; // u
			++offset;
			vertices[j + offset] = v; // v

			indices[i] = (short) i;
		}

		// mesh.render(GL10.GL_TRIANGLE_FAN)
		Mesh mesh = new Mesh(true, vertices.length, indices.length,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));

		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		return mesh;
	}

}
