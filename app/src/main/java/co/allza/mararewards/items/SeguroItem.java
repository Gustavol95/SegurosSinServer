package co.allza.mararewards.items;

/**
 * Created by Tavo on 13/06/2016.
 */
public class SeguroItem {
    private String poliza;
    private String aseguradora;
    private String seguro;
    private String beneficiario;
    private String renovacion;
    private String emergencia;
    private int polizaIcono;
    private int aseguradoraIcono;
    private int seguroIcono;
    private int beneficiarioIcono;
    private int renovacionIcono;
    private int emergenciaIcono;

    public SeguroItem(String poliza, String aseguradora, String seguro, String beneficiario, String renovacion, String emergencia) {
        this.poliza = poliza;
        this.aseguradora = aseguradora;
        this.seguro = seguro;
        this.beneficiario = beneficiario;
        this.renovacion = renovacion;
        this.emergencia = emergencia;
    }

    public SeguroItem(String poliza, String aseguradora, String seguro, String beneficiario, String renovacion, String emergencia, int polizaIcono, int aseguradoraIcono, int seguroIcono, int beneficiarioIcono, int renovacionIcono, int emergenciaIcono) {
        this.poliza = poliza;
        this.aseguradora = aseguradora;
        this.seguro = seguro;
        this.beneficiario = beneficiario;
        this.renovacion = renovacion;
        this.emergencia = emergencia;
        this.polizaIcono = polizaIcono;
        this.aseguradoraIcono = aseguradoraIcono;
        this.seguroIcono = seguroIcono;
        this.beneficiarioIcono = beneficiarioIcono;
        this.renovacionIcono = renovacionIcono;
        this.emergenciaIcono = emergenciaIcono;
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

    public String getSeguro() {
        return seguro;
    }

    public void setSeguro(String seguro) {
        this.seguro = seguro;
    }

    public String getBeneficiario() {
        return beneficiario;
    }

    public void setBeneficiario(String beneficiario) {
        this.beneficiario = beneficiario;
    }

    public String getRenovacion() {
        return renovacion;
    }

    public void setRenovacion(String renovacion) {
        this.renovacion = renovacion;
    }

    public String getEmergencia() {
        return emergencia;
    }

    public void setEmergencia(String emergencia) {
        this.emergencia = emergencia;
    }

    public int getPolizaIcono() {
        return polizaIcono;
    }

    public void setPolizaIcono(int polizaIcono) {
        this.polizaIcono = polizaIcono;
    }

    public int getAseguradoraIcono() {
        return aseguradoraIcono;
    }

    public void setAseguradoraIcono(int aseguradoraIcono) {
        this.aseguradoraIcono = aseguradoraIcono;
    }

    public int getSeguroIcono() {
        return seguroIcono;
    }

    public void setSeguroIcono(int seguroIcono) {
        this.seguroIcono = seguroIcono;
    }

    public int getBeneficiarioIcono() {
        return beneficiarioIcono;
    }

    public void setBeneficiarioIcono(int beneficiarioIcono) {
        this.beneficiarioIcono = beneficiarioIcono;
    }

    public int getRenovacionIcono() {
        return renovacionIcono;
    }

    public void setRenovacionIcono(int renovacionIcono) {
        this.renovacionIcono = renovacionIcono;
    }

    public int getEmergenciaIcono() {
        return emergenciaIcono;
    }

    public void setEmergenciaIcono(int emergenciaIcono) {
        this.emergenciaIcono = emergenciaIcono;
    }
}
