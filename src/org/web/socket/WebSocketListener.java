package org.web.socket;

//import java.net.InetAddress;
//import java.net.InetSocketAddress;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;

public class WebSocketListener implements ServletContextListener{
	
	private static final Logger logger = Logger.getLogger(WebSocketListener.class.getName());
	private Server server = null;
	
	/** Start Embedding Jetty server when WEB Application is started. */
	@Override
	public void contextInitialized(ServletContextEvent event) {
		try {
			// 1) Create a Jetty server with the 8081 port.
			server = new Server(8081);
			
			//InetAddress address = InetAddress.getByName("127.5.78.129");
			//InetAddress address = InetAddress.getByName("localhost");
			
			//InetSocketAddress bindAddr = new InetSocketAddress(address,8081);
		    //InetSocketAddress bindAddr = new InetSocketAddress(address,8081);
		   // int port = 8081;
			//server = new Server(bindAddr);
			
			logger.info("Servidor Jetty con la referencia -> " + server);
			
			// 2) Register SingalingWebSocketHandler in the Jetty server instance.
			SignalingSocketHandler hallSocketHandler = new SignalingSocketHandler();

			hallSocketHandler.setHandler(new DefaultHandler());
			hallSocketHandler.setHandler(server);
			server.setHandler(hallSocketHandler);
			// 2) Start the Jetty server.
			server.start();
			
			System.out.println("Estado del servidor -> " + server.getState());
			
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/** Stop Embedding Jetty server when WEB Application is stopped. */
	@Override
	public void contextDestroyed(ServletContextEvent event) {
		if (server != null) {
			try {// stop the Jetty server.
				logger.info("Servidor Jetty con la referencia -> " + server + " es detenido");
				server.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
				

		
	}
