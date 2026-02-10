package cl.duoc.speedfast.model;

/**
 * Estados válidos para un pedido.
 * El uso de enum mejora la seguridad y legibilidad (evita errores de tipeo).
 */
public enum EstadoPedido {
    PENDIENTE,
    EN_REPARTO,
    ENTREGADO
}