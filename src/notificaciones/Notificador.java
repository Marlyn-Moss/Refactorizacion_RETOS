package notificaciones;

/**
 * Interfaz para el servicio de notificaciones al cliente.
 * 
 * Principio SRP: Aísla la responsabilidad de comunicación con el cliente.
 * Principio OCP: Permite implementar distintos medios de notificación
 * (consola, correo electrónico, SMS) sin alterar GestorPedidos.
 */
public interface Notificador {

    void enviar(String destinatario, String mensaje);
}

