package org.web.socket;

import javax.servlet.http.HttpServletRequest;
import org.eclipse.jetty.websocket.WebSocket;
import org.eclipse.jetty.websocket.WebSocketHandler;

public class SignalingSocketHandler extends WebSocketHandler {

	@Override
	public WebSocket doWebSocketConnect(HttpServletRequest arg0, String arg1) {
		//System.out.println("Content path -> " +  arg0.getContextPath());
		//System.out.println("Sesion -> " +  arg0.getLocalName());
		//System.out.println("Context -> " +  arg0.getAsyncContext());
		//System.out.println("JSON _> " +  JSON.toString(arg0));
		//System.out.println("JSON _> " +  JSON.toString(arg1));
		return new SignalingSocket();
	}

}
