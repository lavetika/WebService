/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dominio;

import com.google.gson.annotations.Expose;
import java.util.ArrayList;

/**
 *
 * @author Alfonso Felix
 */
public class Mensaje {
    @Expose
    private String emisor;
    
    @Expose
    private String contenido;
    
    @Expose
    private String destinatario;

    public Mensaje(String emisor, String contenido) {
        this.emisor = emisor;
        this.contenido = contenido;
    }

    public Mensaje(String emisor, String contenido, String destinatario) {
        this.emisor = emisor;
        this.contenido = contenido;
        this.destinatario = destinatario;
    }
    
    public Mensaje() {
    }

    public String getEmisor() {
        return emisor;
    }

    public void setEmisor(String emisor) {
        this.emisor = emisor;
    }

    public String getCuerpo() {
        return contenido;
    }

    public void setCuerpo(String contenido) {
        this.contenido = contenido;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
}
