package org.web.actions;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.web.model.Hall;
import org.web.model.User;
import org.web.socket.HallProcessingSignal;
import org.webrtc.common.Helper;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

/**
 * Clase Welcome almacena el nombre de usuario y un token
 */
public class Welcome extends ActionSupport implements ModelDriven<User>{
    /**
	 * 
	 */
	
	private static final long serialVersionUID = -5237726610817892384L;
	private static final Logger logger = Logger
			.getLogger(Welcome.class.getName());
	private String message;
    private String userName;
    private User user = new User();
    //private List<User> listUser;
    private static final Hall hall = new Hall();
    public static List<User> listUsers = new ArrayList<User>();
    // Función execute(), por defecto struts busca esta función si no se especifica otra
    public String execute() throws Exception{
        setMessage("Hola usuario " + getUserName());
        
        user.setName(getUserName());
        user.setToken(Helper.generate_random(9));
        listUsers.add(user);
        hall.put(user);
        logger.info("New user -> " + user.getToken() + " Nombre -> " + user.getName());
        for(User a : listUsers){
        	System.out.println("Nombre -> " + a.getName() + " Token ->" + a.getToken());
        }
        return "SUCCESS";
    }
    // Devuelve el mensaje (mensaje + formulario)
    public String getMessage(){
        return message;
    }
    // Asigna el valor del mensaje (mensaje + formulario)
    public void setMessage(String message){
        this.message = message;
    }
    // Devuelve el userName (valor introducido en el formulario)
    public String getUserName(){
        return userName;
    }
    // Guarda el valor del userName
    public void setUserName(String userName){
        this.userName = userName;
    }
	@Override
	public User getModel() {
		
		return this.user;
	}
} 