package descuentos;

/**
 * Implementación de descuento para clientes frecuentes (5% de descuento).
 * El cliente paga el 95% del subtotal.
 * 
 * Cumple LSP: Es intercambiable con DescuentoVIP, DescuentoNormal o cualquier
 * otro Descuento sin alterar el comportamiento de GestorPedidos.
 */
public class DescuentoFrecuente implements Descuento {

    private static final double FACTOR_PAGO = 0.95; // 5% de descuento (paga el 95%)

    @Override
    public double aplicarDescuento(double subtotal) {
        return subtotal * FACTOR_PAGO;
    }
}

