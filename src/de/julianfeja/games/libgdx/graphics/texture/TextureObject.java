package de.julianfeja.games.libgdx.graphics.texture;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;

import daniel.weck.BayazitDecomposer;
import de.julianfeja.games.libgdx.graphics.GeometricObject;
import de.julianfeja.games.libgdx.graphics.Rectangle;
import de.julianfeja.games.libgdx.graphics.defs.BoneDefinition.Direction;

public abstract class TextureObject {
	protected String assetPath;
	protected Texture texture;
	protected Array<Array<Vector2>> polygons;
	protected Mesh mesh;
	protected Rectangle rect;
	protected Vector2 dimension;
	protected int meshMode = GL10.GL_TRIANGLES;
	protected float density = 1.0f;
	protected Array<Vector2> outline;
	private short groupIndex = 0;

	protected boolean staticBody = false;
	protected boolean fixedRotation = false;

	public TextureObject(Pixmap pixmap, Rectangle rect, String assetPath) {
		this.assetPath = assetPath;

		texture = TextureManager.instance().createTexture(assetPath, pixmap);

		dimension = new Vector2(pixmap.getWidth() / GeometricObject.PPM,
				pixmap.getHeight() / GeometricObject.PPM);

		this.rect = rect;
	}

	public TextureObject(TextureObject other, Array<Vector2> outline) {
		texture = other.getTexture();
		this.dimension = other.getDimension();
		this.density = other.getDensity();
		this.groupIndex = other.groupIndex;
		this.fixedRotation = other.fixedRotation;

		this.rect = other.getRect();

		this.outline = outline;

		init(outline);
	}

	protected void init(Array<Vector2> outline) {
		polygons = createPolygons(outline);

		if (meshMode == GL10.GL_TRIANGLES) {
			mesh = createTriangleMesh(triangulate(polygons));
		} else if (meshMode == GL10.GL_TRIANGLE_FAN) {
			mesh = createTextureMesh(outline);
		}

		this.outline = outline;

		trimRectangle(outline);
	}

	public TextureObject setDensity(float density) {
		this.density = density;

		return this;
	}

	public int getMeshMode() {
		return meshMode;
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

	public float getDensity() {
		return this.density;
	}

	public Array<Vector2> getOutline() {
		return this.outline;
	}

	protected void trimRectangle(Array<Vector2> outline) {
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;

		int maxX = 0;
		int maxY = 0;

		for (Vector2 vector2 : outline) {
			if (vector2.x > maxX) {
				maxX = (int) vector2.x;
			}
			if (vector2.y > maxY) {
				maxY = (int) vector2.y;
			}
			if (vector2.x < minX) {
				minX = (int) vector2.x;
			}
			if (vector2.y < minY) {
				minY = (int) vector2.y;
			}
		}

		rect = new Rectangle(minX, minY, maxX - minX, maxY - minY);
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

	protected Mesh createTriangleMesh(Array<Array<Vector2>> polygons) {
		polygons = triangulate(polygons);

		short[] indices = new short[polygons.size * 3];

		int n = 0;

		Array<Vector2> vertices = new Array<Vector2>();

		for (Array<Vector2> polygon : polygons) {
			for (Vector2 vector : polygon) {
				int pos = addOrFind(vertices, vector);
				indices[n++] = (short) pos;
			}
		}

		int stride = 5;
		float[] verticesArray = new float[vertices.size * stride];

		n = 0;

		for (Vector2 vect : vertices) {
			int i = stride * n++;

			verticesArray[i++] = vect.x;
			verticesArray[i++] = vect.y;
			verticesArray[i++] = 0;

			float u = (vect.x) / texture.getWidth();
			float v = (vect.y) / texture.getHeight();

			verticesArray[i++] = u; // u
			verticesArray[i++] = v; // v
		}

		Mesh mesh = new Mesh(true, verticesArray.length, indices.length,
				new VertexAttribute(Usage.Position, 3, "a_position"),
				new VertexAttribute(Usage.TextureCoordinates, 2, "a_texCoords"));

		mesh.setVertices(verticesArray);
		mesh.setIndices(indices);

		return mesh;
	}

	protected int addOrFind(Array<Vector2> vertices, Vector2 vector) {
		int pos = vertices.indexOf(vector, true);
		if (pos == -1) {
			vertices.add(vector.cpy());
			return vertices.size - 1;
		} else {
			return pos;
		}
	}

	protected Array<Array<Vector2>> triangulate(
			Array<Array<Vector2>> oldPolygons) {
		Array<Array<Vector2>> polygons = new Array<Array<Vector2>>();

		for (Array<Vector2> oldPolygon : oldPolygons) {
			if (oldPolygon.size >= 3) {
				Vector2 firstPoint = oldPolygon.get(0);

				for (int i = 1; i < oldPolygon.size - 1; i++) {
					Array<Vector2> polygon = new Array<Vector2>();

					polygon.add(firstPoint.cpy());
					polygon.add(oldPolygon.get(i).cpy());
					polygon.add(oldPolygon.get(i + 1).cpy());

					if (BayazitDecomposer.IsCounterClockWise(polygon)) {
						polygon.reverse();
					}

					polygons.add(polygon);
				}
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

		for (int i = 0; i < outline.size; i++) {
			Vector2 vect = outline.get(i);
			int j = i * stride;
			vertices[j++] = vect.x;
			vertices[j++] = vect.y;
			vertices[j++] = 0;

			float u = (vect.x) / texture.getWidth();
			float v = (vect.y) / texture.getHeight();

			vertices[j++] = u; // u
			vertices[j++] = v; // v

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

	public short getGroupIndex() {
		return groupIndex;
	}

	public TextureObject setGroupIndex(short groupIndex) {
		this.groupIndex = groupIndex;

		return this;
	}

	public Array<TextureObject> cut(Vector2 cutPoint1, Vector2 cutPoint2) {
		Array<TextureObject> ret = new Array<TextureObject>(2);

		Vector2 linePoint1 = outline.get(outline.size - 1);
		Vector2 linePoint2 = null;

		Array<Vector2> outline1 = new Array<Vector2>();
		Array<Vector2> outline2 = new Array<Vector2>();

		Vector2 intersection1 = null;
		Vector2 intersection2 = null;

		for (int i = 0; i < outline.size; i++) {
			linePoint2 = outline.get(i);

			Vector2 intersection = getIntersectionPoint(linePoint1, linePoint2,
					cutPoint1, cutPoint2);

			if (intersection == null) {
				if (intersection1 == null || intersection2 != null) {
					outline1.add(linePoint2);
				} else {
					outline2.add(linePoint2);
				}
			} else {
				if (intersection != null) {
					if (intersection1 == null) {
						intersection1 = intersection;
						outline1.add(intersection);
						outline2.add(intersection);
						outline2.add(linePoint2);
					} else if (intersection2 == null) {
						intersection2 = intersection;
						outline2.add(intersection);
						outline1.add(intersection);
						outline1.add(linePoint2);
					}
				}
			}

			linePoint1 = outline.get(i);
		}

		if (outline1.size < 3 || outline2.size < 3) {
			return null;
		}

		ret.add(new SimpleOutlinedTextureObject(this, outline1));
		ret.add(new SimpleOutlinedTextureObject(this, outline2));

		return ret;
	}

	public Array<TextureObject> cut(Array<Array<Vector2>> cutLines, int index) {
		Array<TextureObject> ret;
		Array<Vector2> cutLine = cutLines.get(index);
		Array<TextureObject> cuts = cut(cutLine.get(0), cutLine.get(1));

		if (cuts == null) {
			return null;
		}

		if (index == cutLines.size - 1) {
			ret = new Array<TextureObject>();

			ret.add(cuts.get(0));
			ret.add(cuts.get(1));
		} else {
			ret = cuts.get(1).cut(cutLines, index + 1);

			if (ret == null) {
				ret = cuts.get(0).cut(cutLines, index + 1);

				ret.insert(0, cuts.get(1));
			} else {
				ret.insert(0, cuts.get(0));
			}

		}

		return ret;
	}

	public Array<TextureObject> cut(Array<Array<Vector2>> cutLines) {
		return cut(cutLines, 0);
	}

	public void normalize(Direction direction) {
		int index = 0;
		float min = Float.MAX_VALUE;

		for (int i = 0; i < outline.size; i++) {
			Vector2 v = outline.get(i);
			if (direction == Direction.Horizontal) {
				if (v.x < min) {
					min = v.x;
					index = i;
				}
			} else if (direction == Direction.Vertical) {
				if (v.y < min) {
					min = v.y;
					index = i;
				}
			}
		}

		if (index != 0) {
			Array<Vector2> newOutline = new Array<Vector2>(outline.size);

			for (int i = 0; i < outline.size; i++) {
				if (index >= outline.size) {
					index = 0;
				}
				newOutline.add(outline.get(index));

				index++;
			}

			outline = newOutline;
		}
	}

	protected Vector2 getIntersectionPoint(Vector2 a1, Vector2 a2, Vector2 b1,
			Vector2 b2) {
		float x1, x2, y1, y2, q2, q1, u2, u1;
		x1 = a1.x;
		x2 = a2.x;

		y1 = a1.y;
		y2 = a2.y;

		q1 = b1.x;
		q2 = b2.x;

		u1 = b1.y;
		u2 = b2.y;

		float n = (y2 - y1) * (q2 - q1) - (u2 - u1) * (x2 - x1);

		if (n != 0) {
			float a = (u2 - u1) * (x1 - q1) + (u1 - y1) * (q2 - q1) / n;

			float xS = x1 + a * (x2 - x1);
			float yS = y1 + a * (y2 - y1);
			if ((((xS >= x1) && (xS <= x2)) || ((xS >= x2) && (xS <= x1)))
					&& (((xS >= q1) && (xS <= q2)) || ((xS >= q2) && (xS <= q1)))) {

				return new Vector2(xS, yS);
			}
		}

		return null;
	}

	public boolean getStaticBody() {
		return staticBody;
	}

	public boolean getFixedRotation() {
		return fixedRotation;
	}

	public TextureObject setStaticBody(boolean staticBody) {
		this.staticBody = staticBody;
		return this;
	}

	public TextureObject setFixedRotation(boolean fixedRotation) {
		this.fixedRotation = fixedRotation;
		return this;
	}

}
