package calculo;

import descuentos.Descuento;

/**
 * Clase encargada de calcular el total final de un pedido.
 * Primero aplica el descuento al subtotal y posteriormente suma el IVA.
 * 
 * Principio SRP: Su única responsabilidad es coordinar el cálculo matemático
 * del total a pagar.
 * Principio OCP y LSP: Trabaja con la abstracción Descuento sin usar if/else,
 * permitiendo cualquier implementación válida de Descuento.
 */
public class CalculadoraTotal {

    private final CalculadoraIVA calculadoraIVA;

    public CalculadoraTotal(CalculadoraIVA calculadoraIVA) {
        this.calculadoraIVA = calculadoraIVA;
    }

    /**
     * Calcula el total final: aplica el descuento al subtotal y luego suma el IVA (13%).
     *
     * @param subtotal  Monto original del pedido antes de descuento.
     * @param descuento Estrategia de descuento a aplicar.
     * @return Total final con descuento e IVA incluidos.
     */
    public double calcularTotal(double subtotal, Descuento descuento) {
        double montoConDescuento = descuento.aplicarDescuento(subtotal);
        return calculadoraIVA.agregarIVA(montoConDescuento);
    }
}

