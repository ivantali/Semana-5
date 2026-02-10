# SpeedFast — Sincronizando procesos en sistemas concurrentes (Java)

Simulación concurrente para la empresa **SpeedFast**, donde múltiples repartidores retiran pedidos desde una **zona de carga compartida** y los entregan en paralelo, aplicando **mecanismos de sincronización** para evitar condiciones de carrera y **entregas duplicadas**.

---

## Objetivo

Diseñar e implementar un sistema orientado a objetos en Java que:

- Modele **Pedidos** con estados (PENDIENTE → EN_REPARTO → ENTREGADO).
- Coordine **múltiples hilos** (repartidores) trabajando en paralelo.
- Proteja el recurso compartido **ZonaDeCarga** mediante sincronización, garantizando que:
  - Cada pedido sea retirado por un único repartidor.
  - Los pedidos se retiren de uno en uno y de forma segura.
- Genere una salida por consola clara, organizada y verificable por hilo.

---

## Tecnologías y conceptos utilizados

- Java (POO)
- Concurrencia:
  - `Thread`
  - `Runnable`
  - `synchronized`
  - `wait()` / `notifyAll()` (patrón productor/consumidor)
- Diseño por capas (MVC extendido):
  - **Model**: Entidades y enums
  - **Repository**: Recurso compartido sincronizado
  - **Service**: Reglas de negocio
  - **Controller**: Orquestación de hilos
  - **View**: Salida por consola centralizada

---

## Estructura de paquetes
```
cl.duoc.speedfast
├─ Main.java
├─ model
│  ├─ Pedido.java
│  └─ EstadoPedido.java
├─ repository
│  └─ ZonaDeCarga.java
├─ service
│  └─ DespachoService.java
├─ controller
│  ├─ DespachoController.java
│  └─ Repartidor.java
└─ view
   └─ ConsoleView.java
```
---

## Descripción funcional

1. **Los pedidos llegan** a una zona de carga común (`ZonaDeCarga`).
2. **Los repartidores** (hilos) retiran pedidos **de uno en uno** desde el recurso compartido.
3. Al retirar un pedido:
   - Se marca como **EN_REPARTO**.
   - Se simula el tiempo de entrega (`Thread.sleep`).
   - Luego se marca como **ENTREGADO**.
4. La sincronización garantiza que:
   - Ningún pedido sea retirado dos veces.
   - No existan entregas duplicadas.
5. La salida por consola permite verificar la ejecución concurrente.

---

## Comportamiento concurrente y sincronización

### ZonaDeCarga (recurso compartido)
`ZonaDeCarga` encapsula la cola FIFO de pedidos y controla el acceso concurrente:

- `agregarPedido(Pedido p)` es `synchronized` y llama `notifyAll()` para despertar consumidores.
- `retirarPedido()` es `synchronized` y:
  - Si no hay pedidos y la recepción está abierta, espera con `wait()`.
  - Si no hay pedidos y la recepción se cerró, retorna `null` para finalizar los repartidores.

Esto implementa un patrón **productor/consumidor** evitando condiciones de carrera.

### Control adicional (robustez)
`DespachoService` registra IDs entregados con una estructura concurrente para detectar intentos de duplicidad, y puede validar transiciones de estado.

---
## Uso académico / educativo.
