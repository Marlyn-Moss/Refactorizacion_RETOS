package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa un pedido realizado por un cliente.
 * 
 * Principio SRP: Su única responsabilidad es almacenar y proveer acceso
 * a los datos propios del pedido (correo, lista de productos y subtotal).
 */
public class Pedido {

    private final String correo;
    private final List<String> items;
    private final double subtotal;

    public Pedido(String correo, List<String> items, double subtotal) {
        this.correo = correo;
        this.items = (items != null) ? new ArrayList<>(items) : new ArrayList<>();
        this.subtotal = subtotal;
    }

    public String getCorreo() {
        return correo;
    }

    public List<String> getItems() {
        return Collections.unmodifiableList(items);
    }

    public double calcularSubtotal() {
        return subtotal;
    }
}

