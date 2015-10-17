package org.web.actions;

import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Logger;
import org.web.model.User;
import org.webrtc.common.Helper;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/**
 * Clase Welcome almacena el nombre de usuario y un token
 */
public class Welcome extends ActionSupport implements ModelDriven<User> {
	/**
	 * 
	 */

	private static final long serialVersionUID = -5237726610817892384L;
	private static final Logger logger = Logger.getLogger(Welcome.class
			.getName());
	private String message;
	private String userName;
	private User user = new User();
	public static ConcurrentMap<String, User> userList = new ConcurrentHashMap<String, User>();

	// Función execute(), por defecto struts busca esta función si no se
	// especifica otra
	public String execute() throws Exception {
		setMessage("Hola usuario " + getUserName());
		user.setName(getUserName());
		user.setToken(Helper.generate_random(16));
		userList.put(user.getToken(), user);
		for (Entry<String, User> user : userList.entrySet()) {
			System.out.println("Usuarios conectados: Nombre -> "
					+ user.getValue().getName() + " | Token ->"
					+ user.getValue().getToken());
		}
		setUserList(userList);
		logger.info("Nuevo usuario conectado: " + user.getName() + " token "
				+ user.getToken());
		return "SUCCESS";
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}

	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}

	/**
	 * @return the user
	 */
	public User getUser() {
		return user;
	}

	/**
	 * @param user the user to set
	 */
	public void setUser(User user) {
		this.user = user;
	}

	/**
	 * @return the userList
	 */
	public static ConcurrentMap<String, User> getUserList() {
		return userList;
	}

	/**
	 * @param userList the userList to set
	 */
	public static void setUserList(ConcurrentMap<String, User> userList) {
		Welcome.userList = userList;
	}

	@Override
	public User getModel() {

		return this.user;
	}
}