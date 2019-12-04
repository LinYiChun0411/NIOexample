package bio;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class BioClient {
	public static void main(String [] arg) {
		
		try {
			Socket socket = new Socket("127.0.0.1", 6789);
			new Thread( ()-> {
						while (true) {
							byte[] b = new byte[1024];
							try {
								int read = socket.getInputStream().read(b);
								System.out.println(new String(b));
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
			).start();
			
			while(true) {
				Scanner scanner = new  Scanner(System.in);
				while(scanner.hasNextLine()) {
					String s = scanner.nextLine();
					try {
						socket.getOutputStream().write(s.getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}	
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

}
