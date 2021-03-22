package websocket;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dominio.Mensaje;
import dominio.Usuario;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.CloseReason;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import jdk.nashorn.internal.parser.JSONParser;

/**
 *
 * @author Alfonso Felix
 */
@ServerEndpoint("/websocket")
public class WebsocketEP {

    private static Map<Session, String> clients
            = Collections.synchronizedMap(new HashMap<Session, String>());

    @OnOpen
    public void onOpen(Session sesion) {
        System.out.println("Open Connection ...");
        //al conectarse un cliente se abre el websocket y se guarda su sesión.

        String nombreUsuario = sesion.getQueryString();

        for (Map.Entry<Session, String> entry : clients.entrySet()) {
            if (entry.getValue().equals(nombreUsuario)) {
                try {
                    sesion.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "El nombre de usuario " + nombreUsuario + " ya está ocupado."));
                    return;
                } catch (IOException ex) {
                    Logger.getLogger(WebsocketEP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        clients.put(sesion, nombreUsuario);
        enviarNombresUsuarios();

    }

    private void enviarNombresUsuarios() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

        if (clients.size() > 1) {
            for (Map.Entry<Session, String> entryUno : clients.entrySet()) {
                Session sesion = entryUno.getKey();
                Usuario[] usuarios = new Usuario[clients.size() - 1];

                int i = 0;

                for (Map.Entry<Session, String> entry : clients.entrySet()) {
                    if (!entry.getKey().getId().equals(sesion.getId())) {
                        usuarios[i] = new Usuario(entry.getKey().getId(), entry.getValue());
                        i++;
                    }
                }

                String strJsonArray = gson.toJson(usuarios, Usuario[].class);
                String strJson = String.format("{\n"
                        + "\"tipo\":\"conexion\",\n"
                        + "\"users\":%s\n"
                        + "}", strJsonArray);
                try {
                    sesion.getBasicRemote().sendText(strJson);
                } catch (IOException ex) {
                    Logger.getLogger(WebsocketEP.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @OnClose
    public void onClose(Session sesion) {
        System.out.println("Close Connection ...");
        //al cerrarse la conexión por parte del cliente se elimina su sesión en el servidor
        clients.remove(sesion);
    }

    @OnMessage
    public void onMessage(String mensajeJson, Session sesion) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Mensaje mensaje = gson.fromJson(mensajeJson, Mensaje.class);
        synchronized (clients) {
            String strJson = String.format("{\n"
                                    + "\"tipo\":\"mensaje\",\n"
                                    + "\"cuerpo\":%s\n"
                                    + "}", gson.toJson(mensaje, Mensaje.class));
            if (mensaje.getDestinatario().equalsIgnoreCase("todos")) {
                for (Map.Entry<Session, String> entry : clients.entrySet()) {
                    Session client = entry.getKey();
                    if (!client.equals(sesion)) {
                        try {
                            
                            client.getBasicRemote().sendText(strJson);
                        } catch (IOException ex) {
                            System.out.println(ex);
                        }
                    }
                }
            } else {
                for (Map.Entry<Session, String> entry : clients.entrySet()) {
                    Session cliente=entry.getKey();
                    if(cliente.getId().equals(mensaje.getDestinatario())){
                        try {
                            cliente.getBasicRemote().sendText(strJson);
                        } catch (IOException ex) {
                            System.out.println(ex);
                        }
                        break;
                    }
                }
            }

        }
    }

    @OnError
    public void onError(Throwable e) {
        e.printStackTrace();
    }

}
