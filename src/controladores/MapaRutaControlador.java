package controladores;

import com.fasterxml.jackson.core.type.TypeReference;
import modelos.ListaParadas;
import modelos.Nodo;
import org.jxmapviewer.viewer.GeoPosition;
import servicios.ArchivoServicio;
import vistas.MapaRutaVista;
import vistas.Marcador;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MapaRutaControlador {

    private final MapaRutaVista vista;
    private final ListaParadas ruta;

    public MapaRutaControlador(MapaRutaVista vista) {
        this.vista = vista;
        this.ruta = new ListaParadas();

        // Conectamos cada botón de la vista con su método
        this.vista.setAlSeleccionarNodo(() -> alSeleccionarNodo());
        this.vista.setAgregarParada(e -> agregarParada());
        this.vista.setModificarParada(e -> modificarParada());
        this.vista.setEliminarParada(e -> eliminarParada());
        this.vista.setGuardarRuta(e -> guardarRuta());
        this.vista.setCargarRuta(e -> cargarRuta());
        this.vista.setCalcularDistancia(e -> calcularDistanciaTotal());

        mostrarParadas();
    }

    private void alSeleccionarNodo() {
        Nodo seleccionado = vista.getNodoSeleccionado();
        if (seleccionado == null) {
            return;
        }
        vista.setUbicacionMapa(seleccionado.getLatitud(), seleccionado.getLongitud());
    }

    private void agregarParada() {
        Nodo nueva = pedirDatosParada(null);
        if (nueva == null) {
            return;
        }
        ruta.agregarAlFinal(nueva);
        mostrarParadas();
    }

    private void modificarParada() {
        Nodo seleccionada = vista.getNodoSeleccionado();
        if (seleccionada == null) {
            vista.mostrarMensaje("Modificar parada", "Debe seleccionar una parada para modificarla.");
            return;
        }

        Nodo datos = pedirDatosParada(seleccionada);
        if (datos == null) {
            return;
        }

        seleccionada.setNombre(datos.getNombre());
        seleccionada.setLatitud(datos.getLatitud());
        seleccionada.setLongitud(datos.getLongitud());
        seleccionada.setTiempoEstimadoLlegada(datos.getTiempoEstimadoLlegada());
        seleccionada.setDescripcion(datos.getDescripcion());
        mostrarParadas();
    }

    private void eliminarParada() {
        Nodo seleccionada = vista.getNodoSeleccionado();
        if (seleccionada == null) {
            vista.mostrarMensaje("Eliminar parada", "Debe seleccionar una parada para eliminarla.");
            return;
        }

        ruta.eliminar(seleccionada);
        mostrarParadas();
        vista.mostrarMensaje("Eliminar parada", "La parada fue eliminada de la ruta.");
    }

    private void guardarRuta() {
        String archivo = ArchivoServicio.elegirArchivo(true);
        if (archivo == null || archivo.isBlank()) {
            return;
        }

        boolean guardado = ArchivoServicio.guardarJson(archivo, ruta.obtenerTodos());
        if (guardado) {
            vista.mostrarMensaje("Guardar ruta", "La ruta se guardó correctamente en formato JSON.");
        } else {
            vista.mostrarMensaje("Guardar ruta", "No fue posible guardar la ruta.");
        }
    }

    private void cargarRuta() {
        String archivo = ArchivoServicio.elegirArchivo(false);
        if (archivo == null || archivo.isBlank()) {
            return;
        }

        List<Nodo> cargadas = ArchivoServicio.leerJson(archivo, new TypeReference<List<Nodo>>() {});
        if (cargadas == null) {
            vista.mostrarMensaje("Cargar ruta", "No se pudo cargar la ruta desde el archivo seleccionado.");
            return;
        }

        ruta.cargarDesdeLista(cargadas);
        mostrarParadas();
        vista.mostrarMensaje("Cargar ruta", "La ruta se cargó correctamente.");
    }

    private void calcularDistanciaTotal() {
        if (ruta.estaVacia()) {
            vista.mostrarMensaje("Distancia total", "La ruta no tiene paradas para calcular.");
            return;
        }

        double total = 0.0;
        List<Nodo> paradas = ruta.obtenerTodos();

        for (int i = 1; i < paradas.size(); i++) {
            Nodo anterior = paradas.get(i - 1);
            Nodo actual = paradas.get(i);
            total += calcularDistanciaKm(anterior.getLatitud(), anterior.getLongitud(),
                                         actual.getLatitud(), actual.getLongitud());
        }

        vista.mostrarMensaje("Distancia total", String.format("La distancia aproximada de la ruta es: %.2f km", total));
    }

    // Actualiza el árbol y los marcadores del mapa con lo que hay en la lista
    private void mostrarParadas() {
        List<Nodo> paradas = ruta.obtenerTodos();
        vista.setNodosArbol(paradas);
        vista.mostrarMarcadores(convertirMarcadores(paradas));
    }

    private Set<Marcador> convertirMarcadores(List<Nodo> paradas) {
        Set<Marcador> marcadores = new HashSet<>();
        for (Nodo parada : paradas) {
            marcadores.add(new Marcador(new GeoPosition(parada.getLatitud(), parada.getLongitud()), parada.getNombre()));
        }
        return marcadores;
    }

    // Muestra un formulario. Si "referencia" es null es para agregar; si no, para modificar.
    private Nodo pedirDatosParada(Nodo referencia) {
        String textoDescripcion = "";
        if (referencia != null && referencia.getDescripcion() != null) {
            textoDescripcion = referencia.getDescripcion();
        }

        JTextField campoNombre = new JTextField(referencia != null ? referencia.getNombre() : "");
        JTextField campoLatitud = new JTextField(referencia != null ? String.valueOf(referencia.getLatitud()) : "0.0");
        JTextField campoLongitud = new JTextField(referencia != null ? String.valueOf(referencia.getLongitud()) : "0.0");
        JTextField campoTiempo = new JTextField(referencia != null ? String.valueOf(referencia.getTiempoEstimadoLlegada()) : "0.0");
        JTextArea campoDescripcion = new JTextArea(textoDescripcion, 4, 20);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(new JLabel("Nombre de la parada:"));
        panel.add(campoNombre);
        panel.add(new JLabel("Latitud:"));
        panel.add(campoLatitud);
        panel.add(new JLabel("Longitud:"));
        panel.add(campoLongitud);
        panel.add(new JLabel("Tiempo estimado de llegada (min):"));
        panel.add(campoTiempo);
        panel.add(new JLabel("Descripción opcional:"));
        panel.add(new JScrollPane(campoDescripcion));

        int opcion = JOptionPane.showConfirmDialog(vista, panel, "Parada de ruta",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (opcion != JOptionPane.OK_OPTION) {
            return null;
        }

        try {
            String nombre = campoNombre.getText().trim();
            if (nombre.isBlank()) {
                throw new IllegalArgumentException("El nombre de la parada es obligatorio.");
            }

            double latitud = Double.parseDouble(campoLatitud.getText().trim());
            double longitud = Double.parseDouble(campoLongitud.getText().trim());
            double tiempo = Double.parseDouble(campoTiempo.getText().trim());
            String descripcion = campoDescripcion.getText().trim();

            return new Nodo(nombre, latitud, longitud, tiempo, descripcion);
        } catch (NumberFormatException ex) {
            vista.mostrarMensaje("Datos inválidos", "Las coordenadas y el tiempo estimado deben ser números válidos.");
            return null;
        } catch (IllegalArgumentException ex) {
            vista.mostrarMensaje("Datos inválidos", ex.getMessage());
            return null;
        }
    }

    // Fórmula de Haversine: distancia en km entre dos coordenadas
    private double calcularDistanciaKm(double lat1, double lon1, double lat2, double lon2) {
        double radioTierraKm = 6371.0;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1Rad) * Math.cos(lat2Rad)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return radioTierraKm * c;
    }
}