package org.web.socket;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import javax.websocket.server.ServerEndpoint;
import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.websocket.WebSocket;
import org.json.JSONException;
import org.json.JSONObject;
import org.web.actions.Welcome;

@ServerEndpoint(value = "/")
public class HallProcessingSignal implements WebSocket.OnTextMessage {

	private static final Logger logger = Logger
			.getLogger(HallProcessingSignal.class.getName());
	private static final ConcurrentMap<String, HallProcessingSignal> channels = new ConcurrentHashMap<String, HallProcessingSignal>();
	private String userHallToken;
	private String userName;
	private Connection connection;

	/**
	 * Método para enviar la señal de conexión de un usuario a la sala Principal
	 * 
	 * @param userToken
	 * @param userName
	 */
	private static void addUserToHall(String userToken, String userName) {

		// Envia el mensaje de conexión en la sala Principal en broadcast
		for (Entry<String, HallProcessingSignal> a : channels.entrySet()) {
			HallProcessingSignal ws = a.getValue();
			String tokens = a.getKey();
			if (!userToken.equals(tokens)) {
				logger.info("Enviando mensaje para los usuarios -> "
						+ JSON.toString(tokens));
				JSONObject json = new JSONObject();
				try {
					json.put("type", "newuser");
					json.put("username", userName);
					json.put("usertoken", userToken);
					ws.sendMessageOut(json.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Método para enviar la señal de deconexión del usuario a los demás
	 * usuarios de la sala Principal
	 * 
	 * @param userToken
	 */
	private static void deleteFromHall(String userToken) {

		// Envia el mensaje de desconexión a la sala Principal en broadcast
		for (Entry<String, HallProcessingSignal> a : channels.entrySet()) {
			HallProcessingSignal ws = a.getValue();
			String token = a.getKey();
			if (!userToken.equals(token)) { // Asi mismo no se envía el mensaje
				logger.info("Enviando señal de desconexión a los usuarios -> "
						+ JSON.toString(ws));
				JSONObject json = new JSONObject();
				try {
					json.put("type", "deleteuser");
					json.put("usertoken", userToken);
					ws.sendMessageOut(json.toString());
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Método para llamar a un usuario para realizar la llamada VoIP, el usuario
	 * entra en la sala y envia la peticion a otro usuario
	 * @param calling
	 * @param to
	 */
	private static void callingToUser(String from, String userName, String to) {
		HallProcessingSignal ws = channels.get(to);
		if (ws != null) {
			JSONObject json = new JSONObject();
			try {
				json.put("type", "calling");
				json.put("username", userName);
				json.put("sender", from);
				json.put("recive", to);
				ws.sendMessageOut(json.toString());
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * Método para enviar el mensaje final
	 * 
	 * @param message
	 */
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

		channels.remove(userHallToken, this);
		Welcome.userList.remove(userHallToken);
		deleteFromHall(userHallToken);
		logger.info("Conexion cerrada -> Token:" + userHallToken);
	}

	@Override
	public void onOpen(Connection connection) {

		logger.info("Conexion abierta");

		this.connection = connection;

	}

	/*
	 * Método para señalizar las peticiones recibidas
	 */
	@Override
	public void onMessage(String data) {
		System.out.println("Se recibe -> " + data);
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(data);

			if (!jsonObject.isNull("type")) {

				if (jsonObject.get("type").equals("connect")) {
					userName = jsonObject.getString("username");
					userHallToken = jsonObject.getString("token");
					channels.put(userHallToken, this);
					addUserToHall(userHallToken, userName);
				}
				if (jsonObject.get("type").equals("calling")) {
					String from = jsonObject.getString("from");
					String to = jsonObject.getString("to");
					String username = jsonObject.getString("username");
					callingToUser(from, username, to);
				}
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
	// public boolean send(String message) {
	//
	// boolean success = false;
	// if (session != null) {
	// try {
	// session.getBasicRemote().sendText(message);
	// /*
	// * Set <Session> allSessions = session.getOpenSessions(); for
	// * (Session sess: allSessions){ try{
	// * if(this.session.getId()==sess.getId()){
	// * sess.getBasicRemote().sendText(message); }
	// *
	// * } catch (IOException ioe) {
	// * System.out.println(ioe.getMessage()); } }
	// */
	// // success = true;
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// return success;
	// }

}