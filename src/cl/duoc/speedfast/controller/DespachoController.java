package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.repository.ZonaDeCarga;
import cl.duoc.speedfast.service.DespachoService;
import cl.duoc.speedfast.view.ConsoleView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Controlador: orquesta la ejecución concurrente de los repartidores usando Thread.
 */
public class DespachoController {

    private final ZonaDeCarga zonaDeCarga;
    private final DespachoService despachoService;
    private final ConsoleView view;

    public DespachoController(ZonaDeCarga zonaDeCarga, DespachoService despachoService, ConsoleView view) {
        this.zonaDeCarga = Objects.requireNonNull(zonaDeCarga, "zonaDeCarga");
        this.despachoService = Objects.requireNonNull(despachoService, "despachoService");
        this.view = Objects.requireNonNull(view, "view");
    }

    /**
     * Crea e inicia hilos (Thread) para cada repartidor.
     *
     * @param nombresRepartidores nombres de repartidores
     * @return lista de hilos iniciados
     */
    public List<Thread> iniciarRepartidores(List<String> nombresRepartidores) {
        Objects.requireNonNull(nombresRepartidores, "nombresRepartidores");
        if (nombresRepartidores.isEmpty()) {
            throw new IllegalArgumentException("Debe existir al menos un repartidor.");
        }

        List<Thread> hilos = new ArrayList<>();
        int idx = 1;

        view.print("Iniciando " + nombresRepartidores.size() + " repartidores en paralelo.");

        for (String nombre : nombresRepartidores) {
            Repartidor runnable = new Repartidor(nombre, zonaDeCarga, despachoService, view);
            Thread t = new Thread(runnable, "Repartidor-Thread-" + (idx++));
            hilos.add(t);
            t.start();
        }

        return hilos;
    }

    /**
     * Espera la finalización de todos los hilos.
     *
     * @param hilos hilos a esperar
     */
    public void esperarFinalizacion(List<Thread> hilos) {
        Objects.requireNonNull(hilos, "hilos");

        for (Thread t : hilos) {
            try {
                t.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                view.print("Hilo principal interrumpido al esperar finalización.");
                break;
            }
        }

        view.print("Todos los hilos de repartidores han finalizado.");
    }
}
