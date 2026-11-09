package descuentos;

/**
 * Implementación de descuento para clientes VIP (15% de descuento).
 * El cliente paga el 85% del subtotal.
 * 
 * Cumple LSP: Puede sustituir a cualquier otra implementación de Descuento
 * en GestorPedidos manteniendo el contrato esperado.
 */
public class DescuentoVIP implements Descuento {

    private static final double FACTOR_PAGO = 0.85; // 15% de descuento (paga el 85%)

    @Override
    public double aplicarDescuento(double subtotal) {
        return subtotal * FACTOR_PAGO;
    }
}

