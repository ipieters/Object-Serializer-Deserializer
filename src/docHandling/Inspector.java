package docHandling;

import java.lang.reflect.Array;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * 
 * @author igorpieters
 *
 */

public class Inspector {

	public void inspect(Object obj, boolean recursive) {
		Class c = obj.getClass();
		inspectClass(c, obj, recursive, 0);
	}

	public void inspectClass(Class c, Object obj, boolean recursive, int depth) {
		inspectClassName(c);
		inspectSuperClass(c, obj, recursive, depth);
		inspectInterface(c, obj, recursive, depth);
		inspectConstructor(c, obj, depth);
		inspectMethods(c, depth);
		inspectFields(c, obj, recursive, depth);
	}

	public void inspectClassName(Class c) {
		printHelper("Class Name [" + c.getName() + "]", 0);
	}
	public void inspectSuperClass(Class c, Object obj, boolean recursive, int depth) {

		if (c.equals(Object.class))
			return;

		Class superClass = c.getSuperclass();

		if (superClass != null) {
			printHelper("SuperClass [" + superClass.getName() + "]", depth);
			inspectClass(superClass, obj, recursive, depth + 1);
		} else
			printHelper("There is no super class", depth);

	}

	public void inspectInterface(Class c, Object obj, boolean recursive, int depth) {

		Class[] interfaces = c.getInterfaces();

		for (Class inter : interfaces) {
			printHelper("Interface [" + inter.getName() + "]", depth);
			inspectClass(inter, obj, recursive, depth + 1);
		}
	}

	public void inspectConstructor(Class c, Object obj, int depth) {

		Constructor[] constructors = c.getDeclaredConstructors();

		for (Constructor constructor : constructors) {
			printHelper("Constructor [" + constructor.getName() + "]", depth);

			Class[] parameters = constructor.getParameterTypes();
			for (Class parameter : parameters)
				printHelper("Parameter [" + parameter.getName() + "]", depth + 1);

			printHelper("Modifier [" + Modifier.toString(constructor.getModifiers()) + "]", depth + 1);
		}
	}

	public void inspectMethods(Class c, int depth) {

		Method[] methods = c.getDeclaredMethods();

		for (Method method : methods) {
			printHelper("Method [" + method.getName() + "]", depth);

			Class[] exceptions = method.getExceptionTypes();
			for (Class exception : exceptions)
				printHelper("Exceptions [" + exception.getName() + "]", depth + 1);

			Class[] parameters = method.getParameterTypes();
			for (Class parameter : parameters)
				printHelper("Parameter [" + parameter.getName() + "]", depth + 1);

			printHelper("Return [" + method.getReturnType().getName() + "]", depth + 1);
			printHelper("Modifier [" + Modifier.toString(method.getModifiers()) + "]", depth + 1);
		}
	}

	public void inspectFields(Class c, Object obj, boolean recursive, int depth) {

		Field[] fields = c.getDeclaredFields();

		for (Field field : fields) {
			printHelper("Field Name [" + field.getName() + "]", depth);
			printHelper("Type [" + field.getType().getName() + "]", depth + 1);
			printHelper("Modifier [" + Modifier.toString(field.getModifiers()) + "]", depth + 1);

			field.setAccessible(true);

			Object value = null;
			try {
				value = field.get(obj);
			} catch (IllegalAccessException e) {
				System.out.println("Unable to access field");
			}

			if (value == null)
				printHelper("Value [" + null + "]", depth + 1);
			else if (field.getType().isArray())
				inspectArray(field.getType(), value, recursive, depth);
			else if (field.getType().isPrimitive())
				printHelper("Value [" + value + "]", depth + 1);
			else
				inspectReferenceValue(recursive, depth, value);
		}
	}

	public void inspectArray(Class c, Object obj, boolean recursive, int depth) {

		Class componentType = c.getComponentType();
		printHelper("Component Type [" + componentType.getName() + "]", depth);
		printHelper("Array Length [" + Array.getLength(obj) + "]", depth);

		for (int i = 0; i < Array.getLength(obj); i++) {
			Object object = Array.get(obj, i);

			if (object == null)
				printHelper("Value [" + i + "] = null", depth + 1);
			else if (componentType.isPrimitive())
				printHelper("Value [" + i + "] = " + object.getClass().getName(), depth + 1);
			else if (componentType.isArray())
				inspectArray(object.getClass(), object, recursive, 1);
			else
				inspectReferenceValue(recursive, depth, object);

		}
		System.out.println();
	}

	private void inspectReferenceValue(boolean recursive, int depth, Object object) {
		if (recursive)
			inspectClass(object.getClass(), object, recursive, depth + 1);
		else
			printHelper("Reference Value [" + object.getClass().getName() + "@" + object.hashCode() + "]", depth);

	}

	public void printHelper(String message, int indent) {
		for (int i = 0; i < indent; i++)
			System.out.print("\t");

		System.out.println(message);
	}
}


 /** Name: Victor Mendoza
 * Course: CPSC501
 * Tutorial: T04
 * Instructor: Leonard Manzara
 * Assignment #: 2
 * Date: Mar. 8th, 2019
 * File: Inspector.java
 * 
 * Credit to: Jordan Kidney
 * 	- Some of the code taken from the ObjectInspector.java file created by 
 * 	  Jordan which was originally a demo object inspector for the Ass2TestDriver
 * 
 * Purpose:
 * 	- Provides functionality to fully introspect a given object i.e. a
 * 	  general-purpose object inspector
 * 
 * Details:
 * 	- Prints the following information (taken from specifications):
 *		- The name of the declaring class
 *		- The name of the immediate superclass
 *		- The name of the interfaces the class implements
 *		- The methods the class declares and:
 *			- The exceptions thrown
 *			- The parameter types
 *			- The return type
 *			- The modifiers
 *		- The constructors the class declares and:
 *			- The parameter types
 *			- The modifiers
 *		- The fields the class declares and:
 *			- The type
 *			- The modifiers
 *		- The current value of each field. 
 *			-If the field is an object reference, 
 *		  	 and recursive is set to false, it prints out the "reference value" 
 *		  	 directly (name of the object�s class plus the object's 
 *		  	 "identity hash code").
 *
 *	- Also traverses the methods, fields and constructors of the class hierarchy
 *	  and also traverses it's interface hierarchy
 *	- If an object or class has already been inspected, it will not be fully inspected
 *		- e.g. an object that already had it's class inspected will only have
 *			   its fields inspected
 *
 

import java.util.*;
import java.lang.reflect.*;

public class Inspector {
	private static final int DIVIDER_WIDTH = 50;
	private Hashtable<String, Boolean> classMap = new Hashtable<>();
	private Hashtable<Integer, Boolean> objMap = new Hashtable<>();
	private Formatter format = new Formatter(DIVIDER_WIDTH);

	public Inspector() {
	}

	/**
	 * fully inspects a given object printing the following:
	 * 	- Declaring Class
	 * 	- Immediate Superclass
	 * 	- Interfaces and it's interface hierarchy
	 * 	- Methods and their info
	 * 	- Constructors and their info
	 * 	- Fields and their info
	 * 		- If recursive is set to true, it inspects the object fields as well
	 * 		- Prints reference otherwise
	 * 	- Arrays and their info
	 * 	- The class hierarchy
	 * If a given object or class had its contents already inspected,
	 * it doesn't print the info
	 * @param obj to inspect
	 * @param recursive - inspect object fields if true, print values otherwise
	 *
	public void inspect(Object obj, boolean recursive) {
		Vector<Object> objectsToInspect = new Vector<>();
		Class<?> objClass = obj.getClass();
		
		// inspect the current class
		if(!objMap.containsKey(System.identityHashCode(obj))) {
			objMap.put(System.identityHashCode(obj), true);
			if (!objClass.isArray()) {
				System.out.println("Declaring Class: " + obj);
				if (objClass.getSuperclass() != null)
					System.out.println("Superclass: " + objClass.getSuperclass().getName());
				inspectContents(obj, objClass, objectsToInspect, recursive);
			}
			else
				inspectArray(obj, obj.toString(), recursive);
		}
		else
			System.out.println("Object already inspected");
	}
	
	/**
	 * prints class info as specified in inspect() documentation
	 * and also handles if classes have already been inspected
	 *
	private void inspectContents(Object obj, Class<?> objClass, Vector<Object> objectsToInspect, boolean recursive) {
		if(!classMap.containsKey(objClass.getName())) {
			inspectInterfaces(objClass);
			inspectMethods(objClass);
			inspectConstructors(objClass);
		}
		else
			System.out.println("Class already inspected");
		
		inspectFields(obj, objClass, objectsToInspect, recursive);
		inspectSuperclasses(obj, objClass, recursive);
		if(!classMap.containsKey(objClass.getName())) {
			inspectInterfaceHierarchy(objClass.getInterfaces(), objClass.getName());
			classMap.put(objClass.getName(), true);
		}
		else
			System.out.println(objClass.getName() + " interfaces already inspected");
	}
	
	/**
	 * recursively inspects the interface hierarchy of a class
	 * @param iArray - interface array
	 * @param className - of hierarchy to be inspected of
	 *
	private void inspectInterfaceHierarchy(Class<?>[] iArray, String className) {
		if(iArray != null && iArray.length >= 1) {
			format.printDivider('I');
			System.out.println(
					"Inspecting interface hierarchy of " + className + ":");
			for(Class<?> interf : iArray) {
				if(!classMap.containsKey(interf.getName())) {
					classMap.put(interf.getName(), true);
					inspectInterface(interf, className);
				}
				else 
					System.out.println(interf.getName() + " already inspected");
				inspectInterfaceHierarchy(interf.getInterfaces(), className);
			}
			format.printDivider('I');
		}
	}
	
	/**
	 * a helper function for inspectInterfaceHierarchy() by printing  all relevant
	 * information for a specific interface
	 * @param interf - interface ot inspect
	 * @param className - class that inherits the interface
	 *
	private void inspectInterface(Class<?> interf, String className) {
		System.out.println("Interface: " + interf.getName());
		Field[] fArr = interf.getDeclaredFields();
		for(Field f : fArr) {
			System.out.println("Field - " + f.getName());
			printFieldInfo(f);
		}
		inspectMethods(interf);
		inspectConstructors(interf);
	}
	
	/**
	 * recursive function for inspecting the superclasses of an object
	 * @param obj - current object inspecting
	 * @param objClass - superclass of object
	 * @param recursive - inspect object fields if true, value otherwise
	 *
	private void inspectSuperclasses(Object obj, Class<?> objClass, boolean recursive) {
		Class<?> superClass = objClass.getSuperclass();
		Vector<Object> objectsToInspect = new Vector<>();
		if(superClass != null) {
			if (!classMap.containsKey(superClass.getName())) {
				format.printDivider('s');
				System.out.println(
						"Inspecting Superclass " + objClass.getSuperclass().getName() +
						" of " + obj.toString());
				inspectContents(obj, objClass.getSuperclass(), objectsToInspect, recursive);
				format.printDivider('s');
			}
		}
	}
	
	/**
	 * inspects a given already build vector array of fields that were objects
	 * in the inspection of an object
	 * @param obj - that had the fields
	 * @param objClass - object's class
	 * @param objectsToInspect - built array of objects to inspect
	 * @param recursive - inspect object fields if true, value otherwise
	 *
	public void inspectFieldClasses(Object obj, Class<?> objClass, Vector<Object> objectsToInspect, boolean recursive) {
		if (objectsToInspect.size() > 0)
			System.out.println("\nInspecting Field Classes of " + objClass.toString() + ":\n");

		Enumeration<?> en = objectsToInspect.elements();
		while (en.hasMoreElements()) {
			Field f = (Field) en.nextElement();
			try {
				format.printDivider('*');
				System.out.println("Field: " + f.getName());
				if (f.get(obj) != null)
					inspect(f.get(obj), recursive);
				else
					System.out.println("null");
				format.printDivider('*');
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * inspects fields of an object and prints information of it
	 * @param obj - object that contained the fields
	 * @param objClass - object's class
	 * @param objectsToInspect - array of non-primitive fields
	 * @param recursive - inspect object fields if true, value otherwise
	 *
	public void inspectFields(Object obj, Class<?> objClass, Vector<Object> objectsToInspect, boolean recursive) {
		Field[] fArray = objClass.getDeclaredFields();
		if (fArray.length >= 1) {
			System.out.println("Fields: ");
			for (Field f : fArray) {
				f.setAccessible(true);
				try {
					if (!f.getType().isPrimitive())
						objectsToInspect.addElement(f);
					format.printIndentedString((f.getName()), 1);
					format.printDivider('-');
					if(f.get(obj) != null)
						format.printIndentedString(("Value: " + f.get(obj)), 2);
					format.printDivider('-');
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (recursive)
				inspectFieldClasses(obj, objClass, objectsToInspect, recursive);
		}
	}
	
	/**
	 * print type and modifiers of a given field
	 * @param f - field to print info about
	 *
	private void printFieldInfo(Field f) {
		format.printIndentedString(("Type: " + f.getType()), 2);
		format.printIndentedString(("Modifiers: " + Modifier.toString(f.getModifiers())), 2);
	}
	
	/**
	 * prints name of all interface the given class implements
	 * @param objClass - to get interfaces from
	 * @return array of interfaces
	 *
	private Class<?>[] inspectInterfaces(Class<?> objClass) {
		Class<?>[] iArray = objClass.getInterfaces();
		if (iArray.length >= 1) {
			System.out.println("Interfaces: ");
			for (Class<?> interf : iArray)
				format.printIndentedString((interf.getName()), 1);
		}
		return iArray;
	}

	/**
	 * prints method information of a given class including:
	 * 	- name, exceptions thrown, parameters, return type and modifiers
	 * @param objClass - to get methods from
	 *
	private void inspectMethods(Class<?> objClass) {
		Method[] mArray = objClass.getDeclaredMethods();
		if (mArray.length >= 1) {
			System.out.println("Methods: ");
			for (Method m : mArray) {
				format.printDivider('m');
				format.printIndentedString((m.getName() + ":"), 1);
				inspectMethodExceps(m);
				inspectMethodParams(m);
				format.printIndentedString(("Return type: " + m.getReturnType()), 2);
				format.printIndentedString(("Modifiers: " + Modifier.toString(m.getModifiers())), 2);
				format.printDivider('m');
			}
			System.out.println();
		}
	}

	private void inspectMethodExceps(Method m) {
		Class<?>[] exceps = m.getExceptionTypes();
		if (exceps.length >= 1) {
			format.printIndentedString(("Exceptions thrown: "), 2);
			printClasses(exceps, 3);
		}
	}

	private void inspectMethodParams(Method m) {
		Class<?>[] params = m.getParameterTypes();
		if (params.length >= 1) {
			format.printIndentedString(("Parameter types: "), 2);
			printClasses(params, 3);
		}
	}

	/**
	 * prints information of the constructors of a given class including:
	 * 	- parameters and modifiers
	 * @param objClass
	 *
	private void inspectConstructors(Class<?> objClass) {
		Constructor<?>[] consArray = objClass.getConstructors();
		int i = 1;
		if (consArray.length >= 1) {
			System.out.println("Constructors: ");
			for (Constructor<?> cons : consArray) {
				format.printDivider('+');
				format.printIndentedString(("Constructor " + i + ":"), 1);
				inspectConstructorParams(cons);
				format.printIndentedString(("Modifiers: " + Modifier.toString(cons.getModifiers())), 2);
				format.printDivider('+');
				i++;
			}
		}
	}
	
	private void inspectConstructorParams(Constructor<?> cons) {
		Class<?>[] params = cons.getParameterTypes();
		if (params.length >= 1) {
			format.printIndentedString(("Parameter types: "), 2);
			printClasses(params, 3);
		}
	}

	/**
	 * prints information about a given array including:
	 * 	- name, component type, length and it's contents
	 * @param arr - array to inspect
	 * @param name - of array
	 * @param recursive - inspect object fields if true, value otherwise
	 *
	private void inspectArray(Object arr, String name, boolean recursive) {
		int length = Array.getLength(arr);
		Class<?> componentType = arr.getClass().getComponentType();
		
		format.printDivider('a');
		System.out.println("Array: " + name);
		format.printIndentedString(("Component type: " + componentType), 2);
		format.printIndentedString(("Length: " + length + '\n' + "Contents: "), 2);
		inspectArrayContents(arr, componentType, length, recursive);
		format.printDivider('a');
	}
	
	/**
	 * prints contents of an array at each index
	 * @param arr - to print
	 * @param componentType - of array
	 * @param length - of array
	 * @param recursive - inspect object fields if true, value otherwise
	 *
	private void inspectArrayContents(Object arr, Class<?> componentType, int length, boolean recursive) {
		Object element;
		for (int i = 0; i < length; i++) {
			element = Array.get(arr, i);
			System.out.print("Index " + i + ": ");
			if (element != null) {
				if (componentType.isPrimitive())
					System.out.println(Array.get(arr, i));
				else {
					format.printDivider('-');
					inspect(element, recursive);
					format.printDivider('-');
				}
			}
			else {
				System.out.println("null");
			}
		}
	}

	/**
	 * prints name of all classes in the given array at a given amount of indents
	 * @param cArray - class array
	 * @param indents
	 *
	private void printClasses(Class<?>[] cArray, int indents) {
		for (Class<?> c : cArray)
			format.printIndentedString((c.getName()), indents);
	}
}
*/