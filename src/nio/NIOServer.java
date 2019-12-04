package nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Scanner;

public class NIOServer implements Runnable{
	private Selector selector;
	private ByteBuffer readBuffer = ByteBuffer.allocate(1024);
	private ByteBuffer writeBuffer = ByteBuffer.allocate(1024);
	
	public static void main(String[] args) {
		new Thread(new NIOServer(9999)).start();;
	}
	public NIOServer(int port) {
		init(port);
	}
	private void init(int port) {
		try {
			System.out.println("server starting at port "+ port +"...");
			//開啟交換器
			this.selector = Selector.open();
			//開啟服務通道
			ServerSocketChannel serverChannel = ServerSocketChannel.open();
			//非阻塞
			serverChannel.configureBlocking(false);
			
			//綁定端口
			serverChannel.bind(new InetSocketAddress(port));
			
			//註冊，並標記當前通道狀態
			serverChannel.register(this.selector, SelectionKey.OP_ACCEPT);
			System.out.println("server started.");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	@Override
	public void run() {
		while(true) {
			try {
				//阻塞方法，當至少一個通道被選中，此方法返回
				//通道是否選擇，由註冊到的交換器中的通道標示決定
				this.selector.select();
				Iterator <SelectionKey> keys = this.selector.selectedKeys().iterator();
				
				while(keys.hasNext()){
					SelectionKey key  = keys.next();
					keys.remove();
					if(key.isValid()) {
						try {
							if(key.isAcceptable()) {
								accept(key);
							}
						}catch(CancelledKeyException cke) {
							//斷開連接，發出異常。
							key.cancel();
						}
						
						//可讀狀態
						try {
							if(key.isReadable()) {
								read(key);
							}
						}catch(CancelledKeyException cke) {
							key.cancel();
						}
						//可寫狀態
						try {
							if(key.isWritable()) {
								write(key);
							}
						}catch(CancelledKeyException cke) {
							key.cancel();
						}
						
						
					}
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			
		}
		
	}
	private void write(SelectionKey key) {
		this.writeBuffer.clear();
		SocketChannel channel = (SocketChannel)key.channel(); 
		Scanner reader = new Scanner (System.in);             
		try {                                                 
			System.out.println("put message for send to client >"); 
			String line = reader.nextLine();                      
			writeBuffer.put(line.getBytes("UTF-8"));              
			writeBuffer.flip();                                   
			channel.write(writeBuffer);
			channel.register(this.selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			e.printStackTrace();
		}
		reader.close();
	}
	private void read(SelectionKey key) {
		try {
			this.readBuffer.clear();
			SocketChannel channel = (SocketChannel) key.channel();
			//將通道中的數據讀取到緩存中，就是客戶端發送的數據。
			int readLength = channel.read(readBuffer);
			
			//檢查客戶端是否寫入數據。
			if( readLength == -1) {
				//關閉通道
				key.channel().close();
				//關閉連接
				key.cancel();
				return;
			}
			/*
			 * filp, NIO中最複雜的操做，就是buffer的控制。
			 *　Buffer 中有一個游標，游標信息在操作後不會歸零，如果直接訪問buffer的話，數據有不一致的可能
			 * flip是重置游標的方法。 
			 */
			this.readBuffer.flip();
			//　Buffer.remaining() -> 是獲取Buffer中有效數據長度的方法
			byte[] datas= new byte[readBuffer.remaining()];
			//將buffer中的有效數據保存到數組中
			readBuffer.get(datas);
			System.out.println("from " + channel.getRemoteAddress() + " client :" + new String(datas,"UTF-8"));
			channel.register(this.selector,SelectionKey.OP_WRITE);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private void accept(SelectionKey key) {
	
		try {
			
			ServerSocketChannel serverChannel =(ServerSocketChannel) key.channel();
			
			SocketChannel channel = serverChannel.accept();
			
			channel.configureBlocking(false);
			
			channel.register(this.selector, SelectionKey.OP_READ);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		
	}


}
