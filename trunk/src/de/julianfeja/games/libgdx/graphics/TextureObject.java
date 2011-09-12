package de.julianfeja.games.libgdx.graphics;

import java.awt.Rectangle;

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

public abstract class TextureObject {
	protected String assetPath;
	protected Texture texture;
	protected Array<Array<Vector2>> polygons;
	protected Mesh mesh;
	protected Rectangle rect;
	protected Vector2 dimension;

	public TextureObject(Pixmap pixmap, Rectangle rect) {
		texture = TextureManager.instance().createTexture(assetPath, pixmap);

		dimension = new Vector2(pixmap.getWidth() / GeometricObject.PPM,
				pixmap.getHeight() / GeometricObject.PPM);

		this.rect = rect;
	}

	public TextureObject(TextureObject other, Array<Vector2> outline) {
		texture = other.getTexture();

		this.rect = other.getRect();

		init(outline);
	}

	protected void init(Array<Vector2> outline) {
		polygons = createPolygons(outline);

		mesh = createTextureMesh(outline);
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

	public Rectangle getRect() {
		return rect;
	}

	public Vector2 getDimension() {
		return dimension;
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

	protected Mesh createTextureMesh(Array<Vector2> outline) {
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

			float u = (vect.x) / texture.getWidth();
			float v = (vect.y) / texture.getHeight();

			++offset;
			vertices[j + offset] = u; // u
			++offset;
			vertices[j + offset] = v; // v

			indices[i] = (short) i;
		}

		Mesh mesh = new Mesh(true, vertices.length, indices.length,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));

		mesh.setVertices(vertices);
		mesh.setIndices(indices);

		return mesh;
	}

	public void dispose() {
		mesh.dispose();
	}

	public TextureObject getCopy(Array<Vector2> outline) {
		return new SimpleOutlinedTextureObject(this, outline);
	}

}
