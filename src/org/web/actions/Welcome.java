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
    //private List<User> userList;
    //private static final Hall hall = new Hall();
    public static ConcurrentMap<String,User> userList = new ConcurrentHashMap<String, User>();
    
    public ConcurrentMap<String,User> getUserList() {
		return userList;
	}
	public void setUserList(ConcurrentMap<String,User> userList) {
		Welcome.userList = userList;
	}
	// Función execute(), por defecto struts busca esta función si no se especifica otra
    public String execute() throws Exception{
        setMessage("Hola usuario " + getUserName());
        user.setName(getUserName());
        user.setToken(Helper.generate_random(9));
        userList.put(user.getToken(), user);
       // hall.put(user);
        //logger.info("New user -> " + user.getToken() + " Nombre -> " + user.getName());
        for(Entry<String, User> user : userList.entrySet()){
        	System.out.println("Usuarios conectados: Nombre -> " + user.getValue().getName() + " | Token ->" + user.getValue().getToken());
        }
        setUserList(userList);
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