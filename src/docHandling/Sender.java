package docHandling;

import java.util.ArrayList;
import java.util.Scanner;
import java.net.Socket;
import java.net.UnknownHostException;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.XMLOutputter;

import objectCreator.ObjectCreator;

import java.io.FileWriter;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.FileInputStream;

/**
 * 
 * @author igorpieters
 *
 */

public class Sender {

	public static void main(String[] args) {
		Sender sender = new Sender();
		Scanner input = new Scanner(System.in);
		ObjectCreator creator = new ObjectCreator();
		ArrayList<Object> objects = creator.runCreator();

		Serializer serializer = new Serializer();


		Document doc = serializer.serialize(objects.get(0));
		System.out.println("Enter filename: ");
		String filename = input.nextLine() + ".xml";
		sender.writeXMLDoc(doc, filename);
		try {
			System.out.println("Enter hostname (localhost for usage on same computer): ");
			String host = input.nextLine();
			sender.sendFile(filename, host);
		} catch (IOException e) {
			e.printStackTrace();
		}

		input.close();
	}


	
	private void writeXMLDoc(Document doc, String filename) {
		XMLOutputter xmlOut = new XMLOutputter();
		try {
			xmlOut.output(doc, new FileWriter(filename));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void sendFile(String filename, String host) throws UnknownHostException, IOException {
		Socket socket = new Socket(host, 5000);
		DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());
		FileInputStream fileInput = new FileInputStream(filename);

		byte[] buffer = new byte[4096];
		int bufferBytes = -1;
		while (true) {
			bufferBytes = fileInput.read(buffer);
			if (bufferBytes <= 0)
				break;
			dataOutput.write(buffer, 0, bufferBytes);
		}

		socket.close();
		fileInput.close();
		dataOutput.close();
	}

}
