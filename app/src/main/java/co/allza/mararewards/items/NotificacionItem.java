package co.allza.mararewards.items;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tavo on 14/06/2016.
 */
public class NotificacionItem extends RealmObject {
    @PrimaryKey
    private int id;
    private int imagen;
    private String titulo;
    private String subtitulo;
    private String valor;

    public NotificacionItem() {
    }



    public NotificacionItem(int imagen, String titulo, String subtitulo, String valor) {
        this.imagen = imagen;
        this.titulo = titulo;

        this.subtitulo = subtitulo;
        this.valor = valor;
    }

    public int getImagen() {
        return imagen;
    }

    public void setImagen(int imagen) {
        this.imagen = imagen;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getSubtitulo() {
        return subtitulo;
    }

    public void setSubtitulo(String subtitulo) {
        this.subtitulo = subtitulo;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
