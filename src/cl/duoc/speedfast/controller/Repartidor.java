package cl.duoc.speedfast.controller;

import cl.duoc.speedfast.model.Pedido;
import cl.duoc.speedfast.repository.ZonaDeCarga;
import cl.duoc.speedfast.service.DespachoService;
import cl.duoc.speedfast.view.ConsoleView;

import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Runnable que representa un repartidor.
 * Retira pedidos desde el recurso compartido (ZonaDeCarga) y aplica reglas del servicio.
 */
public class Repartidor implements Runnable {

    private final String nombre;
    private final ZonaDeCarga zonaDeCarga;
    private final DespachoService despachoService;
    private final ConsoleView view;

    public Repartidor(String nombre, ZonaDeCarga zonaDeCarga, DespachoService despachoService, ConsoleView view) {
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.zonaDeCarga = Objects.requireNonNull(zonaDeCarga, "zonaDeCarga");
        this.despachoService = Objects.requireNonNull(despachoService, "despachoService");
        this.view = Objects.requireNonNull(view, "view");
    }

    @Override
    public void run() {
        view.print("Inicio de jornada para repartidor: " + nombre);

        while (!Thread.currentThread().isInterrupted()) {
            try {
                Pedido pedido = zonaDeCarga.retirarPedido();

                if (pedido == null) {
                    view.print("No hay más pedidos. Finalizando repartidor: " + nombre);
                    break;
                }

                despachoService.marcarEnReparto(pedido);
                view.print("Retirando pedido id=" + pedido.getId() + " | Estado: " + pedido.getEstado() +
                        " | Repartidor: " + nombre);

                int segundos = ThreadLocalRandom.current().nextInt(1, 4);
                view.print("Entregando pedido id=" + pedido.getId() + " (duración simulada: " + segundos + "s) | Repartidor: " + nombre);
                Thread.sleep(segundos * 1000L);

                despachoService.marcarEntregado(pedido);
                view.print("Pedido entregado id=" + pedido.getId() + " | Estado: " + pedido.getEstado() +
                        " | Repartidor: " + nombre);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                view.print("Hilo interrumpido. Finalizando repartidor: " + nombre);
                break;
            } catch (RuntimeException e) {
                view.print("Error en repartidor " + nombre + ": " + e.getMessage());
                break;
            }
        }

        view.print("Fin de jornada para repartidor: " + nombre);
    }
}
