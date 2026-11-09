import java.util.Locale;
import calculo.CalculadoraTotal;
import descuentos.Descuento;
import modelo.Pedido;
import notificaciones.Notificador;
import persistencia.PedidoRepository;
import validacion.ValidadorPedido;

/**
 * Clase coordinadora del proceso de registro de pedidos (Refactorizada con SOLID).
 * 
 * - Cumple SRP: Ya no valida por su cuenta, ni calcula descuentos o IVA, ni se conecta
 *   a una base de datos ni envía correos directamente. Su única responsabilidad es
 *   coordinar el flujo delegando cada tarea a su clase especialista.
 * 
 * - Cumple OCP: No contiene condicionales (if/else ni switch) para evaluar tipos de
 *   cliente. Recibe la interfaz polimórfica Descuento, por lo que está cerrada a la
 *   modificación pero abierta a la extensión cuando se crean nuevos descuentos.
 * 
 * - Cumple LSP: Trabaja con cualquier objeto que implemente la interfaz Descuento
 *   (DescuentoNormal, DescuentoVIP, DescuentoFrecuente, DescuentoEstudiante) sin
 *   alterar su comportamiento ni necesitar conocer el subtipo concreto.
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

