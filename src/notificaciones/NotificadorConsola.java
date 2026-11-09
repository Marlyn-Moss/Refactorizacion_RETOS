package notificaciones;

/**
 * Implementación de Notificador que simula el envío de un correo electrónico
 * mostrando la información directamente en la consola.
 * 
 * Principio SRP: Su única responsabilidad es presentar el mensaje de notificación en consola.
 */
public class NotificadorConsola implements Notificador {

    @Override
    public void enviar(String destinatario, String mensaje) {
        System.out.println("Notificación enviada a: " + destinatario);
        System.out.println("Mensaje: " + mensaje);
    }
}

