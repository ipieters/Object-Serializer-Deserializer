package docHandling;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.IdentityHashMap;

import org.jdom2.Document;
import org.jdom2.Element;

public class Serializer {
	private IdentityHashMap<Object, SerializerHelper> map = new IdentityHashMap<>();
	private int id = 0;

	public Document serialize(Object obj) {
		Document document = new Document();
		Element root = new Element("serialized");
		LinkedList<Object> objectList = new LinkedList<>();

		document.setContent(root);
		objectList.push(obj);
		serializeObj(objectList, root);

		return document;
	}

	private void serializeObj(LinkedList<Object> objectList, Element root) {
		if (!objectList.isEmpty()) {
			Object object = objectList.pop();

			if (!map.get(object).isSerialized() || !map.containsKey(object)) {
				Element xmlObject = new Element("object");
				Class<?> objectClass = object.getClass();

				performMapping(object, true);

				SerializerHelper helper = map.get(object);
				xmlObject.setAttribute("class", objectClass.getName());
				xmlObject.setAttribute("id", new Integer(helper.getId()).toString());

				if (!objectClass.isArray())
					serializeFields(object, objectClass, objectList, xmlObject);
				else {
					xmlObject.setAttribute("length", new Integer(Array.getLength(object)).toString());
					serializeArray(object, objectClass, objectList, xmlObject);
				}

				root.addContent(xmlObject);
			}

			serializeObj(objectList, root);
		}
	}

	private void serializeFields(Object obj, Class<?> objClass, LinkedList<Object> objectList, Element objXML) {
		Field[] fields = objClass.getDeclaredFields();
		Element xmlField;
		for (Field field : fields) {
			field.setAccessible(true);
			try {
				Element value;
				Object fieldObject = field.get(obj);

				xmlField = new Element("field");
				xmlField.setAttribute("name", field.getName());
				xmlField.setAttribute("declaringClass", fieldObject.getClass().getName());

				if (!field.getType().isPrimitive()) {
					value = new Element("reference");
					xmlFieldReference(value, fieldObject, objectList);
				} else {
					value = new Element("value");
					value.setText(fieldObject.toString());
				}

				xmlField.addContent(value);
				objXML.addContent(xmlField);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void serializeArray(Object array, Class<?> objectClass, LinkedList<Object> objectList, Element xmlObject) {
		Class<?> typeClass = objectClass.getComponentType();
		if (!typeClass.isPrimitive()) {
			Object object;
			for (int i = 0; i < Array.getLength(array); i++) {
				Element objectValue = new Element("reference");
				object = Array.get(array, i);
				xmlFieldReference(objectValue, object, objectList);
				xmlObject.addContent(objectValue);
			}
		} else {
			for (int i = 0; i < Array.getLength(array); i++) {
				Element objectValue = new Element("value");
				objectValue.setText(Array.get(array, i).toString());
				xmlObject.addContent(objectValue);
			}
		}
	}

	private void performMapping(Object obj, boolean serialized) {
		if (!map.containsKey(obj)) {
			SerializerHelper helper = new SerializerHelper(id, serialized);
			id++;
			map.put(obj, helper);
		}
	}

	private void xmlFieldReference(Element root, Object obj, LinkedList<Object> objectList) {
		if (obj == null)
			root.setText("null");
		else {
			performMapping(obj, false);
			root.setText(new Integer(map.get(obj).getId()).toString());
			objectList.push(obj);
		}

	}

	private class SerializerHelper {
		private int id = 0;
		private boolean serialized = true;

		public SerializerHelper(int id, boolean serialized) {
			this.id = id;
			this.serialized = true;
		}

		public boolean isSerialized() {
			return serialized;
		}

		public int getId() {
			return id;
		}

	}
}
