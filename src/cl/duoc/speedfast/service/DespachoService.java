package cl.duoc.speedfast.service;

import cl.duoc.speedfast.model.EstadoPedido;
import cl.duoc.speedfast.model.Pedido;

import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Capa de servicio: reglas de negocio del despacho.
 *
 * - Valida transiciones de estado (PENDIENTE -> EN_REPARTO -> ENTREGADO).
 * - Detecta entregas duplicadas por ID para evidenciar robustez.
 */
public class DespachoService {

    private final Set<Integer> entregadosIds = ConcurrentHashMap.newKeySet();

    /**
     * Marca el pedido como EN_REPARTO validando transición.
     *
     * @param pedido pedido a actualizar
     */
    public void marcarEnReparto(Pedido pedido) {
        Objects.requireNonNull(pedido, "pedido");

        EstadoPedido actual = pedido.getEstado();
        if (actual != EstadoPedido.PENDIENTE) {
            throw new IllegalStateException(
                    "Transición inválida a EN_REPARTO. Estado actual=" + actual + " para pedido id=" + pedido.getId()
            );
        }

        pedido.setEstado(EstadoPedido.EN_REPARTO);
    }

    /**
     * Marca el pedido como ENTREGADO validando transición y evitando duplicados.
     *
     * @param pedido pedido a actualizar
     */
    public void marcarEntregado(Pedido pedido) {
        Objects.requireNonNull(pedido, "pedido");

        EstadoPedido actual = pedido.getEstado();
        if (actual != EstadoPedido.EN_REPARTO) {
            throw new IllegalStateException(
                    "Transición inválida a ENTREGADO. Estado actual=" + actual + " para pedido id=" + pedido.getId()
            );
        }

        boolean firstTime = entregadosIds.add(pedido.getId());
        if (!firstTime) {
            throw new IllegalStateException("Entrega duplicada detectada para pedido id=" + pedido.getId());
        }

        pedido.setEstado(EstadoPedido.ENTREGADO);
    }

    public int totalEntregados() {
        return entregadosIds.size();
    }
}
