package org.web.socket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;

import javassist.bytecode.Descriptor.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.util.ajax.JSONObjectConvertor;
import org.eclipse.jetty.websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.web.model.User;

import sun.org.mozilla.javascript.json.JsonParser;

@ServerEndpoint(value = "/")
public class HallProcessingSignal implements WebSocket.OnTextMessage {

	private static final Logger logger = Logger
			.getLogger(HallProcessingSignal.class.getName());
	private static final ConcurrentMap<String, HallProcessingSignal> channels = new ConcurrentHashMap<String, HallProcessingSignal>();
	// private static final Map<String, HallProcessingSignal> channels = new
	// HashMap<String, HallProcessingSignal>();
	// private Connection connection;
	private String userHallToken;
	private String userName;

	private Connection connection;
	private static ArrayList<Session> sessionList = new ArrayList<Session>();
	private Session session;

	/*
	 * public static void send(String token, String message) {
	 * logger.info("Enviando para " + token + " el mensaje (" + message + ") ");
	 * //boolean success = false; for(Entry<String, HallProcessingSignal> a:
	 * channels.entrySet()){
	 * 
	 * 
	 * }
	 * 
	 * }
	 */
	public static void sendToHall(String userToken, String userName) {
		// logger.info("Enviando el mensaje (" + message + ") ");
		// boolean success = false;
		// Envia el mensaje en broadcast
		for (Entry<String, HallProcessingSignal> a : channels.entrySet()) {
			HallProcessingSignal ws = a.getValue();
			String tokens = a.getKey();
			// System.out.println("Token iterator ->" + tokens);
			// System.out.println("UserToken user ->" + userToken);
			if (!userToken.equals(tokens)) {
				logger.info("Enviando mensaje para los usuarios -> "
						+ JSON.toString(ws));
				JSONObject json = new JSONObject();
				try {
					json.put("username", userName);
					json.put("usertoken", userToken);
					ws.sendMessageOut(json.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				// ws.sendHallInfo(userToken);

				// System.out.println("Enviando mensaje para el usuario -> " +
				// JSON.toString(ws));
			}
		}

	}

	/** Send a message out */
	public void sendMessageOut(String message) {

		if (connection != null) {
			try {
				logger.info("Enviando el mensaje ... " + message);
				connection.sendMessage(message);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onClose(int arg0, String arg1) {
		// TODO Auto-generated method stub
		logger.info("Conexion cerrada");
		channels.remove(userHallToken, this);
		// this.connection = null;
		System.out.println("INT -> " + arg0 + " String -> " + arg1);
	}

	@Override
	public void onOpen(Connection connection) {
		// TODO Auto-generated method stub
		logger.info("Conexion abierta");

		this.connection = connection;
		// System.out.println("JSON -> " + JSON.toString(arg0));
		// System.out.println("JSON protocol -> " +
		// JSON.toString(arg0.getProtocol()));
		// System.out.println("JSON binary -> " +
		// JSON.toString(arg0.getMaxBinaryMessageSize()));
	}

	@Override
	public void onMessage(String data) {
		System.out.println("Se recibe -> " + data);
		// System.out.println(JSON.toString(data));
		// System.out.println(JSON.parse(JSON.toString(data)));

		JSONArray jsonArray;
		JSONObject explrObject;
		try {
			jsonArray = new JSONArray(data);
			explrObject = jsonArray.getJSONObject(0);
			if (explrObject.get("username") != null) {
				System.out.println(JSON.toString(explrObject));
				userName = explrObject.getString("username");
				System.out.println(explrObject.getString("token"));
				userHallToken = explrObject.getString("token");
				System.out.println("userHallToken " + userHallToken);
				channels.put(userHallToken, this);
				sendToHall(userHallToken, userName);
			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * @OnOpen public void onOpen(Session session) {
	 * logger.info("Conexion abierta"); // Client (Browser) WebSockets has
	 * opened a connection: Store the opened // connection // this.connection =
	 * connection; this.session = session; sessionList.add(session);
	 * System.out.println(session.getId()); }
	 */
//	public boolean send(String message) {
//
//		boolean success = false;
//		if (session != null) {
//			try {
//				session.getBasicRemote().sendText(message);
//				/*
//				 * Set <Session> allSessions = session.getOpenSessions(); for
//				 * (Session sess: allSessions){ try{
//				 * if(this.session.getId()==sess.getId()){
//				 * sess.getBasicRemote().sendText(message); }
//				 * 
//				 * } catch (IOException ioe) {
//				 * System.out.println(ioe.getMessage()); } }
//				 */
//				// success = true;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//		return success;
//	}

}