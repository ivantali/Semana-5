package cl.duoc.speedfast.model;

import java.util.Objects;

/**
 * Entidad de dominio que representa un pedido.
 * Contiene información básica y estado del flujo de despachos.
 */
public class Pedido {

    private int id;
    private String direccionEntrega;

    /**
     * El estado puede ser observado por diferentes hilos en la salida de consola.
     * Se marca como volatile para asegurar visibilidad entre hilos.
     */
    private volatile EstadoPedido estado;

    /**
     * Crea un pedido con estado inicial PENDIENTE.
     *
     * @param id identificador del pedido
     * @param direccionEntrega dirección de destino
     */
    public Pedido(int id, String direccionEntrega) {
        this.id = id;
        this.direccionEntrega = Objects.requireNonNull(direccionEntrega, "direccionEntrega");
        this.estado = EstadoPedido.PENDIENTE;
    }

    /* Getters */

    public int getId() {
        return id;
    }

    public String getDireccionEntrega() {
        return direccionEntrega;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    /* Setters */

    public void setId(int id) {
        this.id = id;
    }

    public void setDireccionEntrega(String direccionEntrega) {
        this.direccionEntrega = Objects.requireNonNull(direccionEntrega, "direccionEntrega");
    }

    /**
     * Actualiza el estado con el enum.
     * Se sincroniza para mantener consistencia si se accede concurrentemente.
     *
     * @param nuevoEstado nuevo estado
     */
    public synchronized void setEstado(EstadoPedido nuevoEstado) {
        this.estado = Objects.requireNonNull(nuevoEstado, "nuevoEstado");
    }

    /**
     * Permite actualizar el estado desde texto.
     * Convierte el texto a enum validando valores permitidos.
     *
     * @param nuevoEstado texto del estado
     */
    public synchronized void setEstado(String nuevoEstado) {
        Objects.requireNonNull(nuevoEstado, "nuevoEstado");
        try {
            this.estado = EstadoPedido.valueOf(nuevoEstado.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Estado inválido: " + nuevoEstado + ". Use: PENDIENTE, EN_REPARTO, ENTREGADO", ex
            );
        }
    }

    @Override
    public String toString() {
        return "Pedido{id=" + id +
                ", direccionEntrega='" + direccionEntrega + '\'' +
                ", estado=" + estado +
                '}';
    }
}