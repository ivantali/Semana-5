package cl.duoc.speedfast;

import cl.duoc.speedfast.controller.DespachoController;
import cl.duoc.speedfast.model.Pedido;
import cl.duoc.speedfast.repository.ZonaDeCarga;
import cl.duoc.speedfast.service.DespachoService;
import cl.duoc.speedfast.view.ConsoleView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Punto de entrada.
 * Simula llegada progresiva de pedidos y reparto concurrente con 10 repartidores.
 */
public class Main {

    private static final int TOTAL_REPARTIDORES = 10;
    private static final int TOTAL_PEDIDOS = 30;

    public static void main(String[] args) {

        ConsoleView view = new ConsoleView();
        ZonaDeCarga zonaDeCarga = new ZonaDeCarga();
        DespachoService despachoService = new DespachoService();
        DespachoController controller = new DespachoController(zonaDeCarga, despachoService, view);

        view.print("Zona de carga inicializada.");

        // Generar nombres aleatorios de repartidores (
        List<String> repartidores = generarNombresRepartidoresAleatorios(TOTAL_REPARTIDORES);

        // Iniciar repartidores primero (si no hay pedidos, quedarán esperando)
        List<Thread> hilos = controller.iniciarRepartidores(repartidores);

        // Simular llegada progresiva de pedidos (TOTAL_PEDIDOS)
        String[] destinos = {
                "Santiago Centro", "Providencia", "Las Condes", "Maipú", "Ñuñoa",
                "Recoleta", "La Florida", "San Miguel", "Independencia", "Peñalolén"
        };

        for (int id = 1; id <= TOTAL_PEDIDOS; id++) {
            String destino = destinos[(id - 1) % destinos.length];
            Pedido p = new Pedido(id, destino);

            zonaDeCarga.agregarPedido(p);
            view.print("Pedido id=" + id + " agregado. Destino: " + destino);

            // Variación breve para simular llegada no simultánea (50 a 200 ms)
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(50, 201));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                view.print("Hilo principal interrumpido durante la carga de pedidos.");
                break;
            }
        }

        // Indicar que no llegarán más pedidos
        zonaDeCarga.cerrarRecepcion();
        view.print("Recepción de pedidos cerrada.");

        // Esperar fin de hilos
        controller.esperarFinalizacion(hilos);

        // Validación final
        if (zonaDeCarga.pendientes() != 0) {
            view.print("Advertencia: quedaron pedidos pendientes en zona de carga: " + zonaDeCarga.pendientes());
        }

        int entregados = despachoService.totalEntregados();
        if (entregados != TOTAL_PEDIDOS) {
            view.print("Error: total entregados=" + entregados + ", esperado=" + TOTAL_PEDIDOS);
        }

        // Mensaje final
        view.print("Todos los pedidos han sido entregados correctamente.");
        view.print("Total de pedidos entregados: " + entregados);
    }

    /**
     * Genera una lista de nombres aleatorios (sin repetición) para los repartidores.
     * Usa un listado de nombres comunes y selecciona aleatoriamente.
     *
     * @param cantidad cantidad de nombres requeridos
     * @return lista de nombres (tamaño = cantidad)
     */
    private static List<String> generarNombresRepartidoresAleatorios(int cantidad) {
        List<String> nombresBase = new ArrayList<>(List.of(
                "Juan", "Camila", "Pedro", "Javiera", "Matias", "Catalina", "Felipe", "Valentina",
                "Nicolas", "Francisca", "Sebastian", "Antonia", "Diego", "Constanza", "Tomas", "Daniela",
                "Ignacio", "Fernanda", "Joaquin", "Carolina", "Gabriel", "Paula", "Andres", "Natalia",
                "Rodrigo", "Claudia", "Francisco", "Macarena", "Gonzalo", "Andrea", "Pablo", "Isidora",
                "Vicente", "Maria", "Martin", "Cecilia", "Javier", "Veronica", "Mauricio", "Patricia"
        ));

        // Barajar para seleccionar aleatoriamente sin repetición
        Collections.shuffle(nombresBase, ThreadLocalRandom.current());

        // Si la cantidad cabe en la lista base, retornar sublista
        if (cantidad <= nombresBase.size()) {
            return new ArrayList<>(nombresBase.subList(0, cantidad));
        }

        // Si piden más que la lista base, se completa reutilizando con sufijos para mantener unicidad
        List<String> resultado = new ArrayList<>(nombresBase);
        int i = 1;
        while (resultado.size() < cantidad) {
            for (String nombre : nombresBase) {
                resultado.add(nombre + "-" + (++i));
                if (resultado.size() >= cantidad) {
                    break;
                }
            }
        }
        return resultado;
    }
}
