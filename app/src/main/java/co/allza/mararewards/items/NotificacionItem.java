package co.allza.mararewards.items;

/**
 * Created by Tavo on 14/06/2016.
 */
public class NotificacionItem {
    private int imagen;
    private String titulo,subtitulo,valor;

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
}
