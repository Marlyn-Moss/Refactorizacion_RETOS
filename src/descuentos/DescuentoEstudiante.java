package descuentos;

/**
 * Implementación de descuento para clientes estudiantes (10% de descuento).
 * El cliente paga el 90% del subtotal.
 * 
 * Demostración de OCP y LSP (Paso 5):
 * Esta clase fue agregada para soportar el nuevo tipo de cliente ESTUDIANTE
 * SIN modificar Descuento, DescuentoNormal, DescuentoVIP, DescuentoFrecuente
 * ni GestorPedidos.
 */
public class DescuentoEstudiante implements Descuento {

    private static final double FACTOR_PAGO = 0.90; // 10% de descuento (paga el 90%)

    @Override
    public double aplicarDescuento(double subtotal) {
        return subtotal * FACTOR_PAGO;
    }
}

