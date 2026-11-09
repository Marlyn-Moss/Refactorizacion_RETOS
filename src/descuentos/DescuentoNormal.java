package descuentos;

/**
 * Implementación de descuento para clientes normales (sin descuento).
 * El cliente paga el 100% del subtotal.
 * 
 * Cumple LSP: Recibe un subtotal válido y devuelve exactamente el mismo monto
 * sin lanzar excepciones inesperadas ni romper el contrato de Descuento.
 */
public class DescuentoNormal implements Descuento {

    private static final double FACTOR_PAGO = 1.00; // 0% de descuento (paga el 100%)

    @Override
    public double aplicarDescuento(double subtotal) {
        return subtotal * FACTOR_PAGO;
    }
}

