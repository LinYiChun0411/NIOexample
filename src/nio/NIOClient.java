package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NIOClient {
	public static void main(String[] arg){
		InetSocketAddress remote = new InetSocketAddress("localhost", 9999);
		SocketChannel channel = null;
		
		//定義緩存
		ByteBuffer buffer = ByteBuffer.allocate(1024);
		
		try {
			//開啟通道
			channel = SocketChannel.open();
			//連接遠程服務器
			channel.connect(remote);
			Scanner reader = new Scanner(System.in);
			while(true) {
				System.out.println("put message for send to server> ");
				String line = reader.nextLine();
				if(line.equals("exist")) {
					break;
				}
				buffer.put(line.getBytes("UTF-8"));
				buffer.flip();
				//將數據發送給服務器
				channel.write(buffer);
				//清空緩存數據
				buffer.clear();
				
				int readLength = channel.read(buffer);
				if( readLength == -1) {
					break;
				}
				buffer.flip();
				byte[] datas = new byte[buffer.remaining()];
				buffer.get(datas);
				System.out.println("from server : "+ new String(datas, "UTF-8"));
				buffer.clear();
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		                               
		
		
	}

}
