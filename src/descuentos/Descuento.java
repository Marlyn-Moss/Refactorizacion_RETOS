package descuentos;

/**
 * Contrato para el cálculo de descuentos según el tipo de cliente.
 * 
 * Principio OCP (Abierto/Cerrado): Permite agregar nuevos tipos de descuento
 * creando nuevas clases que implementen esta interfaz, sin modificar el código
 * existente en GestorPedidos ni en las demás clases.
 * 
 * Principio LSP (Sustitución de Liskov): Cualquier clase que implemente esta
 * interfaz debe respetar el siguiente contrato:
 * 1. Recibir un subtotal válido (>= 0).
 * 2. Devolver el monto resultante después de aplicar el descuento (entre 0 y subtotal).
 * 3. Mantener un comportamiento consistente sin lanzar excepciones inesperadas
 *    para subtotales válidos.
 * 4. No alterar el estado del pedido.
 */
public interface Descuento {

    /**
     * Calcula y devuelve el monto luego de aplicar el descuento correspondiente.
     *
     * @param subtotal Monto antes de descuento y antes de impuestos.
     * @return Monto con el descuento aplicado.
     */
    double aplicarDescuento(double subtotal);
}

