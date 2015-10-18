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
import org.webrtc.common.Helper;
import org.webrtc.model.Room;

@ServerEndpoint(value = "/")
public class SignalingSocket implements WebSocket.OnTextMessage {

	private static final Logger logger = Logger
			.getLogger(SignalingSocket.class.getName());
	private static final ConcurrentMap<String, SignalingSocket> channels = new ConcurrentHashMap<String, SignalingSocket>();
	private String userHallToken;
	private String userName;
	private Connection connection;
	private String peerToken;

	public static boolean sendPeer(String token, String message) {
		logger.info("Enviando para " +token + " el mensaje ("+message+") ");
		boolean success = false;
		SignalingSocket ws = channels.get(token);
		if(ws!=null) {
			success = ws.send(message);
		}
		return success;
	}
	/**
	 * Método estatico para enviar la señal de conexión de un usuario a la sala Principal
	 * 
	 * @param userToken
	 * @param userName
	 */
	private static void addUserToHall(String userToken, String userName) {

		// Envia el mensaje de conexión en la sala Principal en broadcast
		for (Entry<String, SignalingSocket> a : channels.entrySet()) {
			SignalingSocket ws = a.getValue();
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
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Método estatico para enviar la señal de deconexión del usuario a los demás
	 * usuarios de la sala Principal
	 * 
	 * @param userToken
	 */
	private static void deleteFromHall(String userToken) {

		// Envia el mensaje de desconexión a la sala Principal en broadcast
		for (Entry<String, SignalingSocket> a : channels.entrySet()) {
			SignalingSocket ws = a.getValue();
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
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Método estatico para llamar a un usuario para realizar la llamada VoIP, el usuario
	 * entra en la sala y envia la peticion a otro usuario
	 * @param calling
	 * @param to
	 */
	private static void callingToUser(String from, String userName, String to) {
		SignalingSocket ws = channels.get(to);
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
	private void sendMessageOut(String message) {

		if (connection != null) {
			try {
				logger.info("Enviando el mensaje ... " + message);
				connection.sendMessage(message);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	/**
	 * Método para enviar los datos de WebRTC para la videoconexión
	 * @param message
	 * @return
	 */
	private boolean send(String message) {

		if (connection != null) {
			try {
				logger.info("Enviando el mensaje ... " + message);
				connection.sendMessage(message);
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return false;

	}

	@Override
	public void onClose(int arg0, String arg1) {
		if(userHallToken!=null){
			channels.remove(userHallToken, this);
			Welcome.usersList.remove(userHallToken);
			deleteFromHall(userHallToken);
			logger.info("Conexion cerrada de la sala principal -> Token:" + userHallToken);
		}
		if(peerToken!=null){
			channels.remove(peerToken, this);
			Room.disconnect(peerToken);
			logger.info("VideoConexion cerrada -> Token:" + peerToken);
		}
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
				if (jsonObject.get("type").equals("token")) {
					peerToken = jsonObject.getString("value");
					channels.put(peerToken, this);
					logger.info("Añadido el token (valid="+Helper.is_valid_token(peerToken)+"): "+peerToken);
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
//			try{
//				if(data.startsWith("token")) { // peer declaration
//					int index = data.indexOf(":");
//					peerToken = data.substring(index+1);
//					channels.put(peerToken, this);
//					logger.info("Añadido el token (valid="+Helper.is_valid_token(peerToken)+"): "+peerToken);
//				}else { // signaling messages exchange --> route it to the other peer
//					String room_key = Helper.get_room_key(peerToken);
//					Room room = Room.get_by_key_name(room_key);
//					String user = Helper.get_user(peerToken);
//					String other_user = room.get_other_user(user);
//					String other_token = Helper.make_token(room, other_user);
//					sendPeer(other_token, data);
//				}
//			}catch(Exception ex){
//				ex.printStackTrace();
//			}
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