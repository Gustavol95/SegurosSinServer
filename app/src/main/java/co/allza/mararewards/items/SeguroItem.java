package co.allza.mararewards.items;

/**
 * Created by Tavo on 13/06/2016.
 */
public class SeguroItem {
    String nombre;
    String poliza;
    String aseguradora;
    String cobertura;

    public SeguroItem(String nombre, String poliza, String aseguradora, String cobertura) {
        this.nombre = nombre;
        this.poliza = poliza;
        this.aseguradora = aseguradora;
        this.cobertura = cobertura;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPoliza() {
        return poliza;
    }

    public void setPoliza(String poliza) {
        this.poliza = poliza;
    }

    public String getAseguradora() {
        return aseguradora;
    }

    public void setAseguradora(String aseguradora) {
        this.aseguradora = aseguradora;
    }

    public String getCobertura() {
        return cobertura;
    }

    public void setCobertura(String cobertura) {
        this.cobertura = cobertura;
    }
}
