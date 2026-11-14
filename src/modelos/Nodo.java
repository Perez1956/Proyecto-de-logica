package modelos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Nodo {

    // Datos de la parada
    private String nombre;
    private double latitud;
    private double longitud;
    @JsonProperty("tiempoEstimado")
    private double tiempoEstimadoLlegada;
    private String descripcion;

    // Enlace al siguiente nodo de la lista
    private Nodo siguiente;

    // Constructor vacío: lo necesita Jackson para leer el JSON
    public Nodo() {
    }

    // Constructor con datos: lo usa el controlador
    public Nodo(String nombre, double latitud, double longitud,
                double tiempoEstimadoLlegada, String descripcion) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tiempoEstimadoLlegada = tiempoEstimadoLlegada;
        this.descripcion = descripcion;
        this.siguiente = null;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getTiempoEstimadoLlegada() {
        return tiempoEstimadoLlegada;
    }

    public void setTiempoEstimadoLlegada(double tiempoEstimadoLlegada) {
        this.tiempoEstimadoLlegada = tiempoEstimadoLlegada;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    // @JsonIgnore le dice a Jackson que NO guarde este campo en el JSON.
    // Si no lo pones, intenta guardar toda la cadena de nodos y se vuelve un lío.
    // El orden de las paradas ya lo da la lista, no hace falta guardarlo.
    @JsonIgnore
    public Nodo getSiguiente() {
        return siguiente;
    }

    public void setSiguiente(Nodo siguiente) {
        this.siguiente = siguiente;
    }

    // El JTree usa este método para mostrar el texto de cada parada
    @Override
    public String toString() {
        return nombre;
    }
}