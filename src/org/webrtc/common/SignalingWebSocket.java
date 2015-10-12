package org.webrtc.common;


import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.eclipse.jetty.websocket.WebSocket;
import org.webrtc.model.Room;

@ServerEndpoint(value="/")
public class SignalingWebSocket  {

	private static final Logger logger = Logger.getLogger(SignalingWebSocket.class.getName()); 
	private static final ConcurrentMap<String, SignalingWebSocket> channels = new ConcurrentHashMap<String, SignalingWebSocket>();
	
	//private Connection connection;
	private String token;
	private static ArrayList<Session> sessionList = new ArrayList<Session>();
	private Session session;
	
	public static boolean sendPeer(String token, String message) {
		logger.info("Enviando para " +token + " el mensaje ("+message+") ");
		boolean success = false;
		SignalingWebSocket ws = channels.get(token);
		if(ws!=null) 
			success = ws.send(message);	
		return success;
	}
		
	@OnOpen
	public void onOpen(Session session) {
		logger.info("Conexion abierta");
		// Client (Browser) WebSockets has opened a connection: Store the opened connection
		//this.connection = connection;
		this.session = session;
		sessionList.add(session);
		System.out.println(session.getId());
	}

	/**	check if message is token declaration and then store mapping between the token and this ws. */
	
	@OnMessage
	public void onMessage(String data) {
		try {			
			if(data.startsWith("token")) { // peer declaration
				int index = data.indexOf(":");
				token = data.substring(index+1);
				channels.put(token, this);
				logger.info("Añadido el token (valid="+Helper.is_valid_token(token)+"): "+token);
			}else { // signaling messages exchange --> route it to the other peer
				String room_key = Helper.get_room_key(token);
				Room room = Room.get_by_key_name(room_key);
				String user = Helper.get_user(token);
				String other_user = room.get_other_user(user);
				String other_token = Helper.make_token(room, other_user);
				sendPeer(other_token, data);
			}
		} catch (Exception x) {
			// Error was detected, close the WebSocket client side
			//this.connection.disconnect();
		}
	}

	/** Remove ChatWebSocket in the global list of SignalingWebSocket instance. */
	/*@Override
	public void onClose(int closeCode, String message) {
		logger.info("La conexion (token:"+token+") cerrada con el codigo "+closeCode+" : " + message);
		if(token!=null) { 
			Room.disconnect(token);
			channels.remove(token);
		}
	}*/
	@OnClose
	public void onClose(Session session){
		try {
			this.session.close();
			logger.info("La conexion (token:"+token+") cierra la sesión "+session.getId());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/** Send a message out */
	/*
	public boolean send(String message) {
		logger.info("Enviando el mensaje ... " + message);
		boolean success = false;
		if(connection!=null) {
			try {
				connection.sendMessage(message);
				success = true;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return success;
	}*/
	
	public boolean send(String message) {
		
		boolean success = false;
		if(session!=null) {
			try {
				session.getBasicRemote().sendText(message);
				/*Set <Session> allSessions = session.getOpenSessions();
			     for (Session sess: allSessions){          
			        try{
			        	if(this.session.getId()==sess.getId()){
			        		sess.getBasicRemote().sendText(message);
			        	}
			        	
			          } catch (IOException ioe) {        
			              System.out.println(ioe.getMessage());         
			          }   
			     }*/
			//	success = true;
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
		return success;
	}
	/*@Override
	public void onOpen(Connection connection) {
		logger.info("Conexion abierta");
		// Client (Browser) WebSockets has opened a connection: Store the opened connection
		this.connection = connection;
		
	}*/

}
