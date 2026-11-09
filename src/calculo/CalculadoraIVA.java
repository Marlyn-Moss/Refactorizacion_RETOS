package calculo;

/**
 * Clase encargada exclusivamente del cálculo del Impuesto al Valor Agregado (IVA).
 * 
 * Principio SRP: Su única razón para cambiar es si la tasa del impuesto (13%)
 * o la fórmula de cálculo del IVA cambia por ley.
 */
public class CalculadoraIVA {

    private static final double TASA_IVA = 0.13; // 13% de IVA

    /**
     * Calcula únicamente el monto correspondiente al IVA.
     */
    public double calcularMontoIVA(double montoBase) {
        return montoBase * TASA_IVA;
    }

    /**
     * Devuelve el monto base más el IVA del 13%.
     */
    public double agregarIVA(double montoBase) {
        return montoBase + calcularMontoIVA(montoBase);
    }
}

