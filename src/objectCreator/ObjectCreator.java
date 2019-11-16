package objectCreator;

import java.util.ArrayList;
import java.util.Scanner;

/**
 * 
 * @author igorpieters
 *
 */
public class ObjectCreator {

	ArrayList<Object> objects = null;
	Scanner input = new Scanner(System.in);

	public ObjectCreator() {
		objects = new ArrayList<>();
	}

	public ArrayList<Object> runCreator() {
		System.out.println("Object creator");
		int option = -1;
		do {
			displayObjects();
			System.out.println("1: Create object");
			System.out.println("2: Finish");
			System.out.println("Enter integer:");
			option = input.nextInt();

			if (option == 1)
				runMenu();
			else if (option != 2)
				System.out.println("Invalid option, try again");
		} while (option != 2);
		System.out.println("Serializing objects ...");
		return objects;
	}

	private void displayObjects() {
		if (!objects.isEmpty()) {
			System.out.println("Object List");
			for (int i = 0; i < objects.size(); i++)
				System.out.println("["+ i + ": " + objects.get(i) + "]\n");
		}
	}

	private Object runMenu() {
		Object obj = null;
		menu();
		System.out.println("Enter integer:");
		int option = input.nextInt();

		switch (option) {
		case 1:
			System.out.println("Creating Object of Primitive:");
			obj = addObjA();
			break;
		case 2:
			System.out.println("Creating Object of Object Reference:\n");
			obj = addObjB();
			break;
		case 3:
			System.out.println("Creating Object of Primitive Array: ");
			obj = addObjC();
			break;
		case 4:
			System.out.println("Creating Object of Object Reference Array:\n");
			obj = addObjD();
			break;
		case 5:
			System.out.println("Creating Object of Collection of Objects");
			obj = addObjE();
			break;
		case 6:
			if (!objects.isEmpty())
				obj = referenceExisting();
			else
				System.out.println("List is empty!");
			break;
		case 7:
			obj = null;
			break;
		case 8:
			break;
		default:
			System.out.println("Please enter a valid option");
		}
		return obj;
	}

	private void menu() {
		System.out.println("\nAdd object:");
		System.out.println("1: Object of primitive");
		System.out.println("2: Object of object references");
		System.out.println("3: Object of primitive array");
		System.out.println("4: Object of object reference array");
		System.out.println("5: Object of collection (ArrayList)");
		System.out.println("6: Reference an existing object");
		System.out.println("7: Null Object");
		System.out.println("8: Exit");
	}

	private Object addObjA() {

		System.out.println("Enter Integer: ");
		int integer = input.nextInt();
		ObjectA object = new ObjectA(integer);
		objects.add(object);
		return object;

	}

	private Object addObjB() {

		System.out.println("Create first object to be referenced: ");
		Object obj1 = runMenu();
		System.out.println("Create second object to be referenced: ");
		Object obj2 = runMenu();
		ObjectB object = new ObjectB(obj1, obj2);
		objects.add(object);
		return object;
	}

	private Object addObjC() {
		System.out.print("Enter array size: ");
		int i = input.nextInt();
		int[] intArray = new int[i];

		for (i = 0; i < intArray.length; i++) {
			intArray[i] = i;
		}

		ObjectC object = new ObjectC(intArray);
		objects.add(object);
		return object;

	}

	private Object addObjD() {
		System.out.print("Enter array size: ");
		int i = input.nextInt();
		Object[] objArray = new Object[i];

		for (i = 0; i < objArray.length; i++) {
			System.out.println("\nAdd object [" + i + "] to array:");
			objArray[i] = runMenu();
		}

		ObjectD object = new ObjectD(objArray);
		objects.add(object);
		return object;

	}

	private Object addObjE() {
		ObjectE object = new ObjectE();

		System.out.println("Enter array list length: ");
		int length = input.nextInt();

		Object obj;
		for (int i = 0; i < length; i++) {
			System.out.println("Creating object" + i + " for ArrayList");
			obj = runMenu();
			object.getCollection().add(obj);
		}
		objects.add(object);
		return object;

	}

	private Object referenceExisting() {
		System.out.println("Choose an object to reference: ");
		displayObjects();
		System.out.println("Enter an intenger: ");
		return objects.get(input.nextInt());
	}

}
