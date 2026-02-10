package cl.duoc.speedfast.view;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Vista de consola.
 * Se sincroniza la impresión para evitar intercalado de texto entre hilos.
 */
public class ConsoleView {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public synchronized void print(String mensaje) {
        String time = LocalTime.now().format(TIME_FMT);
        String threadName = Thread.currentThread().getName();
        System.out.println("[" + time + "][" + threadName + "] " + mensaje);
    }
}