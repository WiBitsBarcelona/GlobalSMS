package eu.globaldevelopers.globalsms;

/**
 * Created by Artur on 07/11/2017.
 */

public class operacion {
    private String titulo;
    private String fechahora;
    private String codigo;
    private String producto;
    private String litros;
    private String estado;
    private double diesel_liters;
    private double adblue_liters;
    private double red_liters;
    private double gas_kilos;
    private int service_type;

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public void setFechahora(String fechahora) {
        this.fechahora = fechahora;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public void setLitros(String litros) {
        this.litros = litros;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getTitulo() {
        return this.titulo;
    }

    public String getFechahora() {
        return this.fechahora;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public String getProducto() {
        return this.producto;
    }

    public String getLitros() {
        return this.litros;
    }

    public String getEstado() {
        return this.estado;
    }

    public int getId() {
        return titulo.hashCode();
    }

    public double getDieselLiters() {
        return diesel_liters;
    }

    public void setDieselLiters(double diesel_liters) {
        this.diesel_liters = diesel_liters;
    }

    public double getAdblueLiters() {
        return adblue_liters;
    }

    public void setAdblueLiters(double adblue_liters) {
        this.adblue_liters = adblue_liters;
    }

    public double getRedLiters() {
        return red_liters;
    }

    public void setRedLiters(double red_liters) {
        this.red_liters = red_liters;
    }

    public double getGasKilos() {
        return gas_kilos;
    }

    public void setGasKilos(double gas_kilos) {
        this.gas_kilos = gas_kilos;
    }

    public int getServiceType() {
        return service_type;
    }

    public void setServiceType(int service_type) {
        this.service_type = service_type;
    }
}
