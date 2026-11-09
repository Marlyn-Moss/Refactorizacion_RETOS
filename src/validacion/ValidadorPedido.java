package validacion;

import modelo.Pedido;

/**
 * Clase encargada exclusivamente de validar que un pedido sea correcto.
 * 
 * Principio SRP: Su única razón para cambiar es si se modifican las reglas
 * de validación de un pedido (por ejemplo, requerir un mínimo de productos
 * o validar el formato del correo).
 */
public class ValidadorPedido {

    public void validar(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException("El pedido no puede ser nulo.");
        }

        if (pedido.getItems() == null || pedido.getItems().isEmpty()) {
            throw new IllegalArgumentException("Pedido vacío: debe incluir al menos un producto.");
        }

        if (pedido.calcularSubtotal() <= 0) {
            throw new IllegalArgumentException("El subtotal del pedido debe ser mayor que cero.");
        }

        if (pedido.getCorreo() == null || pedido.getCorreo().trim().isEmpty()) {
            throw new IllegalArgumentException("El correo del cliente es obligatorio.");
        }
    }
}

