package co.allza.mararewards.items;
/**
 * Created by Tavo on 07/06/2016.
 */
public class LoginItem {
    private String texto;
    private int icono;

    public LoginItem(String texto, int icono) {
        this.texto = texto;
        this.icono = icono;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }

    public int getIcono() {
        return icono;
    }

    public void setIcono(int icono) {
        this.icono = icono;
    }
}
