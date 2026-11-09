
import calculo.CalculadoraIVA;
import calculo.CalculadoraTotal;
import descuentos.Descuento;
import descuentos.DescuentoEstudiante;
import descuentos.DescuentoFrecuente;
import descuentos.DescuentoNormal;
import descuentos.DescuentoVIP;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import modelo.Pedido;
import notificaciones.Notificador;
import notificaciones.NotificadorConsola;
import persistencia.PedidoRepository;
import persistencia.PedidoRepositoryMemoria;
import validacion.ValidadorPedido;

/**
 * Clase principal para demostrar el funcionamiento del laboratorio SOLID (SRP,
 * OCP y LSP) con los 4 casos de prueba requeridos.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Inicialización de los componentes con responsabilidad única (SRP)
        ValidadorPedido validador = new ValidadorPedido();
        CalculadoraIVA calculadoraIVA = new CalculadoraIVA();
        CalculadoraTotal calculadoraTotal = new CalculadoraTotal(calculadoraIVA);
        PedidoRepository repositorio = new PedidoRepositoryMemoria();
        Notificador notificador = new NotificadorConsola();

        // 2. Creación del coordinador GestorPedidos
        GestorPedidos gestor = new GestorPedidos(
                validador,
                calculadoraTotal,
                repositorio,
                notificador
        );

        List<String> productosEjemplo = Arrays.asList("Teclado Mecánico", "Mouse Óptico");
        double subtotalEjemplo = 100.00;

        System.out.println("==============================================================");
        System.out.println("   LABORATORIO JAVA - REFACTORIZACIÓN DE GESTORPEDIDOS (SOLID)");
        System.out.println("==============================================================\n");

        // -------------------------------------------------------------------------
        // CASO 1: Cliente Normal (Descuento: 0%, IVA: 13%, Total esperado: $113.00)
        // -------------------------------------------------------------------------
        System.out.println("--- CASO 1: CLIENTE NORMAL ---");
        Pedido pedidoNormal = new Pedido("cliente.normal@gmail.com", productosEjemplo, subtotalEjemplo);
        Descuento descuentoNormal = new DescuentoNormal();
        imprimirDetallePrevia(subtotalEjemplo, "0%", "13%");
        gestor.registrar(pedidoNormal, descuentoNormal);
        System.out.println();

        // -------------------------------------------------------------------------
        // CASO 2: Cliente VIP (Descuento: 15%, IVA: 13%, Total esperado: $96.05)
        // Demostración de LSP: Sustituimos la implementación de Descuento sin
        // modificar absolutamente nada en GestorPedidos.
        // -------------------------------------------------------------------------
        System.out.println("--- CASO 2: CLIENTE VIP ---");
        Pedido pedidoVIP = new Pedido("cliente.vip@gmail.com", productosEjemplo, subtotalEjemplo);
        Descuento descuento = new DescuentoVIP();
        imprimirDetallePrevia(subtotalEjemplo, "15%", "13%");
        gestor.registrar(pedidoVIP, descuento);
        System.out.println();

        // -------------------------------------------------------------------------
        // CASO 3: Cliente Frecuente (Descuento: 5%, IVA: 13%, Total esperado: $107.35)
        // Demostración de LSP (Paso 4): La misma referencia de tipo Descuento
        // ahora apunta a DescuentoFrecuente y GestorPedidos funciona sin cambios.
        // -------------------------------------------------------------------------
        System.out.println("--- CASO 3: CLIENTE FRECUENTE ---");
        Pedido pedidoFrecuente = new Pedido("cliente.frecuente@gmail.com", productosEjemplo, subtotalEjemplo);
        descuento = new DescuentoFrecuente(); // Sustitución directa (LSP)
        imprimirDetallePrevia(subtotalEjemplo, "5%", "13%");
        gestor.registrar(pedidoFrecuente, descuento);
        System.out.println();

        // -------------------------------------------------------------------------
        // CASO 4: Cliente Estudiante (Descuento: 10%, IVA: 13%, Total esperado: $101.70)
        // Demostración de OCP (Paso 5): Se incorporó DescuentoEstudiante sin tocar
        // GestorPedidos, Descuento, ni los descuentos anteriores.
        // -------------------------------------------------------------------------
        System.out.println("--- CASO 4: CLIENTE ESTUDIANTE ---");
        Pedido pedidoEstudiante = new Pedido("estudiante@gmail.com", productosEjemplo, subtotalEjemplo);
        Descuento descuentoEstudiante = new DescuentoEstudiante();
        imprimirDetallePrevia(subtotalEjemplo, "10%", "13%");
        gestor.registrar(pedidoEstudiante, descuentoEstudiante);
        System.out.println();

        System.out.println("==============================================================");
        System.out.println("Total de pedidos almacenados en memoria: " + repositorio.obtenerTodos().size());
        System.out.println("==============================================================");
    }

    private static void imprimirDetallePrevia(double subtotal, String porcentajeDescuento, String porcentajeIVA) {
        System.out.printf(Locale.US, "Subtotal: $%.2f | Descuento: %s | IVA: %s%n",
                subtotal, porcentajeDescuento, porcentajeIVA);
    }
}
