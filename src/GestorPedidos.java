import java.util.Locale;
import calculo.CalculadoraTotal;
import descuentos.Descuento;
import modelo.Pedido;
import notificaciones.Notificador;
import persistencia.PedidoRepository;
import validacion.ValidadorPedido;

/**
 * Clase coordinadora del proceso de registro de pedidos (Refactorizada con los 5 principios SOLID).
 * 
 * - Cumple SRP: Su única responsabilidad es coordinar el flujo de registro delegando
 *   validación, cálculo, persistencia y notificación a clases especializadas.
 * 
 * - Cumple OCP: No contiene condicionales (if/else ni switch) para evaluar tipos de
 *   cliente. Recibe la interfaz polimórfica Descuento, quedando abierta a la extensión
 *   y cerrada a la modificación.
 * 
 * - Cumple LSP: Trabaja con cualquier implementación de Descuento (DescuentoNormal,
 *   DescuentoVIP, DescuentoFrecuente, DescuentoEstudiante) sin alterar su flujo.
 * 
 * - Cumple ISP: Consume interfaces pequeñas y cohesivas (Descuento, PedidoRepository,
 *   Notificador) de las cuales utiliza todos sus métodos.
 * 
 * - Cumple DIP: Depende de abstracciones (PedidoRepository y Notificador) y recibe
 *   todas sus dependencias mediante inyección por constructor, sin instanciar
 *   implementaciones concretas con 'new' dentro de esta clase.
 */
public class GestorPedidos {

    private final ValidadorPedido validador;
    private final CalculadoraTotal calculadoraTotal;
    private final PedidoRepository repositorio;
    private final Notificador notificador;

    public GestorPedidos(
            ValidadorPedido validador,
            CalculadoraTotal calculadoraTotal,
            PedidoRepository repositorio,
            Notificador notificador) {
        this.validador = validador;
        this.calculadoraTotal = calculadoraTotal;
        this.repositorio = repositorio;
        this.notificador = notificador;
    }

    /**
     * Coordina el registro de un pedido aplicando el descuento proporcionado.
     *
     * @param pedido    El pedido a procesar.
     * @param descuento La estrategia de descuento polimórfica según el cliente.
     * @return El monto total calculado (con descuento e IVA incluidos).
     */
    public double registrar(Pedido pedido, Descuento descuento) {
        // 1. Validar que el pedido cumpla con los requisitos
        validador.validar(pedido);

        // 2. Calcular el total aplicando primero el descuento y luego el IVA (13%)
        double total = calculadoraTotal.calcularTotal(pedido.calcularSubtotal(), descuento);

        // 3. Guardar el pedido en el repositorio
        repositorio.guardar(pedido);

        // 4. Enviar notificación al cliente
        notificador.enviar(pedido.getCorreo(), "Pedido recibido correctamente.");

        // 5. Mostrar el total formateado a dos decimales
        System.out.printf(Locale.US, "Total: $%.2f%n", total);

        return total;
    }
}

