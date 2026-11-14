package modelos;

import java.util.ArrayList;
import java.util.List;

public class ListaParadas {

    private Nodo cabeza; // primer nodo de la lista (null si está vacía)

    public ListaParadas() {
        cabeza = null;
    }

    public boolean estaVacia() {
        return cabeza == null;
    }

    // Agrega una parada al final de la ruta
    public void agregarAlFinal(Nodo nuevo) {
        nuevo.setSiguiente(null);

        if (cabeza == null) {
            // Lista vacía: el nuevo nodo es la cabeza
            cabeza = nuevo;
        } else {
            // Recorremos hasta llegar al último nodo
            Nodo actual = cabeza;
            while (actual.getSiguiente() != null) {
                actual = actual.getSiguiente();
            }
            actual.setSiguiente(nuevo);
        }
    }

    // Elimina una parada de la lista
    public void eliminar(Nodo objetivo) {
        if (cabeza == null) {
            return;
        }

        // Caso 1: el nodo a eliminar es la cabeza
        if (cabeza == objetivo) {
            cabeza = cabeza.getSiguiente();
            return;
        }

        // Caso 2: está en medio o al final.
        // Buscamos el nodo ANTERIOR al que queremos eliminar
        Nodo actual = cabeza;
        while (actual.getSiguiente() != null && actual.getSiguiente() != objetivo) {
            actual = actual.getSiguiente();
        }

        // Si lo encontramos, saltamos sobre él
        if (actual.getSiguiente() == objetivo) {
            actual.setSiguiente(objetivo.getSiguiente());
        }
    }

    // Devuelve las paradas en orden, como una List normal
    public List<Nodo> obtenerTodos() {
        List<Nodo> paradas = new ArrayList<>();
        Nodo actual = cabeza;
        while (actual != null) {
            paradas.add(actual);
            actual = actual.getSiguiente();
        }
        return paradas;
    }

    // Vacía la lista y la arma de nuevo con los datos leídos del JSON
    public void cargarDesdeLista(List<Nodo> paradas) {
        cabeza = null;
        for (Nodo parada : paradas) {
            agregarAlFinal(parada);
        }
    }
}