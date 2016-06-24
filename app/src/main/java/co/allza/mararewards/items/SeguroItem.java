package co.allza.mararewards.items;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Tavo on 13/06/2016.
 */
public class SeguroItem extends RealmObject {
    @PrimaryKey
    private int id;
    private int customer_id;
    private String policy;
    private String name;
    private String Description;
    private String insured_name;
    private String expiration;
    private String emergency;
    private String updated_at;
    private int polizaIcono;
    private int aseguradoraIcono;
    private int seguroIcono;
    private int beneficiarioIcono;
    private int renovacionIcono;
    private int emergenciaIcono;
    private int usuario;


    public SeguroItem() {
    }

    public SeguroItem(int id, String poliza, String aseguradora, String seguro, String beneficiario, String renovacion, String emergencia, String updated_at) {
        this.id=id;
        this.policy = poliza;
        this.name = aseguradora;
        this.Description = seguro;
        this.insured_name = beneficiario;
        this.expiration = renovacion;
        this.emergency = emergencia;
        this.updated_at=updated_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getPolicy() {
        return policy;
    }

    public void setPolicy(String policy) {
        this.policy = policy;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getInsured_name() {
        return insured_name;
    }

    public void setInsured_name(String insured_name) {
        this.insured_name = insured_name;
    }

    public String getExpiration() {
        return expiration;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }

    public String getEmergency() {
        return emergency;
    }

    public void setEmergency(String emergency) {
        this.emergency = emergency;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
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

    public int getUsuario() {
        return usuario;
    }

    public void setUsuario(int usuario) {
        this.usuario = usuario;
    }
}
