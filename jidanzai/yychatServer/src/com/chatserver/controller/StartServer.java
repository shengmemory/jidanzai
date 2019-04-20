package com.chatserver.controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import com.yychat.model.Message;
import com.yychat.model.User;

public class StartServer {
	ServerSocket ss;
	Socket s;
	
	public static HashMap hmSocket=new HashMap<String,Socket>();//���ͣ�ͨ����
	String userName;
	public StartServer(){
		try {
			ss=new ServerSocket(3456);//�������˿ڼ���3456
			System.out.println("�������Ѿ�����������3456�˿�...");
			while(true){//?���߳�����
				s=ss.accept();//�ȴ��ͻ��˽�������
				System.out.println(s);//�������Socket����
				
				//�ֽ������� ��װ�� ����������
				ObjectInputStream ois=new ObjectInputStream(s.getInputStream());
				User user=(User)ois.readObject();//�����û���¼����user
				this.userName=user.getUserName();
				System.out.println(user.getUserName());
				System.out.println(user.getPassWord());
				
				//Server����֤�����Ƿ�123456��
				Message mess=new Message();
				mess.setSender("Server");
				mess.setReceiver(user.getUserName());
				if(user.getPassWord().equals("123456")){//������"==",����Ƚ�
					//��Ϣ���ݣ�����һ��Message����				
					mess.setMessageType(Message.message_LoginSuccess);//��֤ͨ��					
				}
				else{				
					mess.setMessageType(Message.message_LoginFailure);//��֤��ͨ��	
				}				
				ObjectOutputStream oos=new ObjectOutputStream(s.getOutputStream());
				oos.writeObject(mess);
				
				if(user.getPassWord().equals("123456")){
					//����ÿһ���û���Ӧ��Socket
					hmSocket.put(userName,s);
					System.out.println("�����û���Socket"+userName+s);
					//��ν��տͻ��˵�������Ϣ����һ���߳�������������Ϣ
					new ServerReceiverThread(s,hmSocket).start();//�����̣߳������߳̾���
					System.out.println("�����̳߳ɹ�");
				}
				
			}			
			
		} catch (IOException | ClassNotFoundException e) {			
			e.printStackTrace();
		}
	}
}
