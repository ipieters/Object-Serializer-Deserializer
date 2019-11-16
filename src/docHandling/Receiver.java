package docHandling;


import java.util.Scanner;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Receiver {

	public static void main(String[] args) {
		SAXBuilder builder = new SAXBuilder();
		Deserializer deserializer = new Deserializer();
		File xmlFile;
		Receiver receiver = new Receiver();
		Inspector inspector = new Inspector();
		
		try {
			receiver.receiveXMlObject();
			xmlFile = new File("deserialize.xml");
			Document document = (Document) builder.build(xmlFile);
			Object object = deserializer.deserialize(document);
			
			System.out.println("XML Object contents");
			inspector.inspect(object, true);
		} 
		catch (JDOMException | IOException e) {
			e.printStackTrace();
		}
	}
	

	private void receiveXMlObject() throws IOException {
		System.out.println("Waiting for connection");
		FileOutputStream fileOut = new FileOutputStream("deserialize.xml", false);
		ServerSocket server = new ServerSocket(5000);
		Socket client = server.accept();
		
		byte[] buffer = new byte[4096];
		int bufferBytes = -1;
		DataInputStream fromSender = new DataInputStream(client.getInputStream());
		while(true) {
			bufferBytes = fromSender.read(buffer);
			if(bufferBytes <= 0) break;
			fileOut.write(buffer, 0, bufferBytes);
		}
		
		fileOut.close();
		server.close();
		client.close();
	}

}
