package notificaciones;

/**
 * Interfaz para el servicio de notificaciones al cliente.
 * 
 * - Principio SRP: Aísla la responsabilidad de comunicación con el cliente.
 * - Principio ISP (Segregación de Interfaces): Contiene únicamente el método
 *   necesario para enviar una notificación, sin mezclar operaciones de persistencia
 *   ni cálculos.
 * - Principio DIP (Inversión de Dependencias): Permite que GestorPedidos dependa
 *   de esta abstracción en lugar de una clase concreta como EmailSender.
 */
public interface Notificador {

    void enviar(String destinatario, String mensaje);
}

