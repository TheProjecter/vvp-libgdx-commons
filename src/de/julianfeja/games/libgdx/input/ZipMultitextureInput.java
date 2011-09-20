package de.julianfeja.games.libgdx.input;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

import daniel.weck.BayazitDecomposer;
import de.julianfeja.games.libgdx.graphics.BoxBodyTexture;
import de.julianfeja.games.libgdx.graphics.JointDefinition;
import de.julianfeja.games.libgdx.graphics.PhysicsObjectGroup;

public class ZipMultitextureInput extends ZipInput {
	protected Pixmap pixmap;
	protected Map<String, Array<Vector2>> outlines;

	protected Array<JointDefinition> jointDefs;

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
			jointDefs = parseJoints(document);

			is.close();
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected static Map<String, Array<Vector2>> parseOutlines(Document document) {
		Map<String, Array<Vector2>> outlines = new LinkedHashMap<String, Array<Vector2>>();

		@SuppressWarnings("rawtypes")
		List bodys = document.selectNodes("//body");

		for (Object bodyObject : bodys) {
			Node bodyNode = (Node) bodyObject;

			String id = bodyNode.valueOf("@id");
			Array<Vector2> outline = new Array<Vector2>();

			@SuppressWarnings("rawtypes")
			List points = bodyNode.selectNodes("./point");

			for (Object pointObject : points) {
				Node pointNode = (Node) pointObject;

				outline.add(parsePoint(pointNode));
			}

			if (!BayazitDecomposer.IsCounterClockWise(outline)) {
				outline.reverse();
			}

			outlines.put(id, outline);
		}

		return outlines;
	}

	protected static Vector2 parsePoint(Node node) {
		float x = Float.parseFloat(node.valueOf("@x"));
		float y = Float.parseFloat(node.valueOf("@y"));

		return new Vector2(x, y);
	}

	protected static Array<JointDefinition> parseJoints(Document document) {
		Array<JointDefinition> jointDefs = new Array<JointDefinition>();

		@SuppressWarnings("unchecked")
		List<JointDefinition> joints = document.selectNodes("//joint");

		for (Object jointObject : joints) {
			Node jointNode = (Node) jointObject;

			String type = jointNode.valueOf("@type");
			String id = jointNode.valueOf("@id");

			@SuppressWarnings("unchecked")
			List<JointDefinition> points = jointNode.selectNodes("./point");

			if (points.size() >= 2) {
				Node pointNode1 = (Node) points.get(0);
				Node pointNode2 = (Node) points.get(points.size() - 1);

				String idBody1 = pointNode1.valueOf("@body");
				String idBody2 = pointNode2.valueOf("@body");

				Vector2 point1 = parsePoint(pointNode1);
				Vector2 point2 = parsePoint(pointNode2);

				jointDefs.add(new JointDefinition(id, idBody1, idBody2, point1,
						point2, type));
			}
		}

		return jointDefs;
	}

	public PhysicsObjectGroup createGroup(int index, Vector2 position,
			Vector2 scale, World world) {
		Map<String, Array<Vector2>> bodys = new LinkedHashMap<String, Array<Vector2>>();
		Array<JointDefinition> joints = new Array<JointDefinition>();

		JointDefinition rootJoint = jointDefs.get(index);

		bodys.put(rootJoint.getIdBody1(), outlines.get(rootJoint.getIdBody1()));
		bodys.put(rootJoint.getIdBody2(), outlines.get(rootJoint.getIdBody2()));

		for (int i = 0; i < jointDefs.size; i++) {
			JointDefinition jointDef = jointDefs.get(i);

			if (bodys.containsKey(jointDef.getIdBody1())
					|| bodys.containsKey(jointDef.getIdBody2())) {
				if (!joints.contains(jointDef, true)) {
					joints.add(jointDef);

					if (!bodys.containsKey(jointDef.getIdBody1()))
						bodys.put(jointDef.getIdBody1(),
								outlines.get(jointDef.getIdBody1()));

					if (!bodys.containsKey(jointDef.getIdBody2()))
						bodys.put(jointDef.getIdBody2(),
								outlines.get(jointDef.getIdBody2()));

					i = 0;
				}
			}
		}

		return new PhysicsObjectGroup(position, scale, bodys, joints,
				new BoxBodyTexture(pixmap), world);
	}

	public PhysicsObjectGroup createGroup(int index, Vector2 position,
			float scale, World world) {
		return createGroup(index, position, new Vector2(scale, scale), world);
	}

	public PhysicsObjectGroup createGroup(int index, Vector2 position,
			World world) {
		return createGroup(index, position, 1f, world);
	}

	public Pixmap getPixmap() {
		return pixmap;
	}

	public Array<Vector2> getOutline(int index) {
		if (index >= 0 && index < outlines.size()) {
			return new ArrayList<Array<Vector2>>(outlines.values()).get(index);
		} else {
			return null;
		}
	}
}
