package com.chatserver.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import javax.xml.crypto.dsig.spec.HMACParameterSpec;

import com.yychat.model.Message;

public class ServerReceiverThread extends Thread{
//必须要有run()方法
	Socket s;
	HashMap hmSocket;
	Message mess;
	String sender;
	ObjectOutputStream oos;
	
	public ServerReceiverThread(Socket s,HashMap hmSocket){
		this.s=s;
		this.hmSocket=hmSocket;		
	}
	
	public void run(){		
		ObjectInputStream ois;
		while(true){
			try {
				//接收Message信息
				ois=new ObjectInputStream(s.getInputStream());
				mess=(Message)ois.readObject();//接收用户发送来的聊天信息，阻塞，10个用户，100毫秒
				System.out.println("等待用户发送聊天信息");
				System.out.println(mess.getSender()+"对"+mess.getReceiver()+"说:"+mess.getContent());
				//转发Message信息
				if(mess.getMessageType().equals(Message.message_Common)){
					Socket s1=(Socket)hmSocket.get(mess.getReceiver());
					sendMessage(s1,mess);
					System.out.println("服务器转发了信息"+mess.getSender()+"对"+mess.getReceiver()+"说:"+mess.getContent());
				}
				
				//第2步骤，返回在线好友信息到客户端
				if(mess.getMessageType().equals(Message.message_RequestOnlineFriend)){
					//首先要拿到在线好友信息
					Set friendSet=StartServer.hmSocket.keySet();
					Iterator it=friendSet.iterator();
					String friendName;
					String friendString ="";
					while(it.hasNext()){
						friendName=(String)it.next();
						if(!friendName.equals(mess.getSender()))//排除自己
							friendString=friendString+friendName+" ";
						}
					System.out.println("全部好友的名字: "+friendString);
					mess.setContent(friendString);
					mess.setReceiver(sender);
					mess.setSender("Server");
					mess.setMessageType(Message.message_OnlineFriend);
					
					Socket s1=(Socket)hmSocket.get(sender);
					sendMessage(s1,mess);
					
					
					
					
							
				}
				
			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
			
		
		
		
	}

	private void sendMessage(Socket s1, Message mess) {
		// TODO Auto-generated method stub
		
	}
	
}
