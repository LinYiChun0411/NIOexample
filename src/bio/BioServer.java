package bio;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {
	public static void main(String [] arg) {
		try {
			ServerSocket serversocket = new ServerSocket(6789);
			Socket socket = serversocket.accept();
			
			new Thread(
					()->{
						while(true) {
							
							try {
								InputStream inputStream =  socket.getInputStream();
								byte[] b = new byte[1024];
								int read = inputStream.read(b);
								System.out.println(new String(b));
//								socket.getOutputStream().write("沒錢".getBytes());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
					).start();
						
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
