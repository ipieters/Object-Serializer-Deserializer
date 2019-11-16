package docHandling;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;

import org.jdom2.DataConversionException;
import org.jdom2.Document;
import org.jdom2.Element;

/**
 * 
 * @author igorpieters
 *
 */

public class Deserializer {
	HashMap<Integer, Object> objectMap = new HashMap<>();

	public Object deserialize(Document document) {
		Element root = document.getRootElement();
		List<Element> xmlObjects = root.getChildren();

		deserializeXML(xmlObjects);

		convertXMLObject(xmlObjects);
		Object object = objectMap.get(0);

		return object;
	}

	private void deserializeXML(List<Element> xmlObjects) {
		for (Element xmlObject : xmlObjects) {
			try {
				Object object = new Object();
				String className = xmlObject.getAttributeValue("class");
				int objectID = xmlObject.getAttribute("id").getIntValue();
				Class<?> objectClass = Class.forName(className);

				if (!objectClass.isArray())
					object = objectClass.newInstance();
				else {
					int length = xmlObject.getAttribute("length").getIntValue();
					object = Array.newInstance(objectClass.getComponentType(), length);
				}
				objectMap.put(objectID, object);
			} catch (DataConversionException | ClassNotFoundException | IllegalAccessException
					| InstantiationException e) {
				e.printStackTrace();
			}
		}
	}

	private void convertXMLObject(List<Element> xmlObjects) {
		for (Element xmlObject : xmlObjects) {
			try {
				int id = xmlObject.getAttribute("id").getIntValue();

				Object object = objectMap.get(id);
				Class<?> objectClass = object.getClass();
				if (object.getClass().isArray()) {
					deserializeArray(object, xmlObject, objectClass);
				} else
					deserializeFields(object, xmlObject, objectClass);
			} catch (DataConversionException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

	}

	private void deserializeFields(Object obj, Element xmlObject, Class<?> objectClass)
			throws IllegalArgumentException, IllegalAccessException {

		objectClass = obj.getClass();
		Field[] fields = objectClass.getDeclaredFields();
		List<Element> xmlFields = xmlObject.getChildren();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			field.setAccessible(true);
			if (!(Modifier.isFinal(field.getModifiers()) || Modifier.isStatic(field.getModifiers()))) {
				Class<?> fieldType = field.getType();
				String value = xmlFields.get(i).getValue();
				if (!value.equals("null")) {
					if (!fieldType.isPrimitive()) {
						Object objectRef = objectMap.get(Integer.parseInt(value));
						field.set(obj, objectRef);
					} else {
						if (fieldType.isAssignableFrom(int.class))
							field.setInt(obj, Integer.parseInt(value));
						if (fieldType.isAssignableFrom(double.class))
							field.setDouble(obj, Double.parseDouble(value));
						if (fieldType.isAssignableFrom(boolean.class))
							field.setBoolean(obj, Boolean.parseBoolean(value));
						if (fieldType.isAssignableFrom(byte.class))
							field.setByte(obj, Byte.parseByte(value));
						if (fieldType.isAssignableFrom(char.class))
							field.setChar(obj, value.toCharArray()[0]);
						if (fieldType.isAssignableFrom(short.class))
							field.setShort(obj, Short.parseShort(value));
						if (fieldType.isAssignableFrom(long.class))
							field.setLong(obj, Long.parseLong(value));
						if (fieldType.isAssignableFrom(float.class))
							field.setFloat(obj, Float.parseFloat(value));
					}
				} else
					field.set(obj, null);
			}
		}

	}

	private void deserializeArray(Object obj, Element xmlObject, Class<?> objClass) {
		List<Element> xmlValues = xmlObject.getChildren();
		Class<?> componentType = objClass.getComponentType();
		for (int i = 0; i < xmlValues.size(); i++) {
			String value = xmlValues.get(i).getValue();
			if (!value.equals("null")) {
				if (!componentType.isPrimitive()) {
					Object objectReference = objectMap.get(Integer.parseInt(value));
					Array.set(obj, i, objectReference);
				} else {
					if (componentType.isAssignableFrom(int.class))
						Array.set(obj, i, Integer.parseInt(value));
					if (componentType.isAssignableFrom(boolean.class))
						Array.set(obj, i, Boolean.parseBoolean(value));
					if (componentType.isAssignableFrom(byte.class))
						Array.set(obj, i, Byte.parseByte(value));
					if (componentType.isAssignableFrom(char.class))
						Array.set(obj, i, value.toCharArray()[0]);
					if (componentType.isAssignableFrom(short.class))
						Array.set(obj, i, Short.parseShort(value));
					if (componentType.isAssignableFrom(long.class))
						Array.set(obj, i, Long.parseLong(value));
					if (componentType.isAssignableFrom(float.class))
						Array.set(obj, i, Float.parseFloat(value));
					if (componentType.isAssignableFrom(double.class))
						Array.set(obj, i, Double.parseDouble(value));
				}
			} else
				Array.set(obj, i, null);
		}
	}
}
