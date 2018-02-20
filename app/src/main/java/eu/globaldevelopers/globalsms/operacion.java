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

}
