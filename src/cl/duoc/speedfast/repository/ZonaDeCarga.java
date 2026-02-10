package cl.duoc.speedfast.repository;

import cl.duoc.speedfast.model.EstadoPedido;
import cl.duoc.speedfast.model.Pedido;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Recurso compartido: ZonaDeCarga.
 *
 * Responsabilidad:
 * - Almacenar pedidos pendientes en una cola FIFO.
 * - Permitir retiro seguro de pedidos, de uno en uno, sin duplicación.
 *
 * Sincronización:
 * - Los métodos críticos son synchronized.
 * - wait/notifyAll implementan un patrón productor/consumidor.
 */
public class ZonaDeCarga {

    private final List<Pedido> cola = new LinkedList<>();
    private boolean recepcionCerrada = false;

    public synchronized void agregarPedido(Pedido pedido) {
        Objects.requireNonNull(pedido, "pedido");

        if (recepcionCerrada) {
            throw new IllegalStateException("Recepción cerrada: no se pueden agregar más pedidos.");
        }

        if (pedido.getEstado() != EstadoPedido.PENDIENTE) {
            pedido.setEstado(EstadoPedido.PENDIENTE);
        }

        cola.add(pedido);
        notifyAll();
    }

    public synchronized Pedido retirarPedido() {
        while (cola.isEmpty() && !recepcionCerrada) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }

        if (cola.isEmpty() && recepcionCerrada) {
            return null;
        }

        return cola.remove(0);
    }

    public synchronized void cerrarRecepcion() {
        recepcionCerrada = true;
        notifyAll();
    }

    public synchronized int pendientes() {
        return cola.size();
    }
}