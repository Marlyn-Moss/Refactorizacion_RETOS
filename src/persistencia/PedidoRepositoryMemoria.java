package persistencia;

import java.util.ArrayList;
import java.util.List;
import modelo.Pedido;

/**
 * Implementación de PedidoRepository que almacena los pedidos en memoria
 * utilizando un ArrayList, simulando una base de datos sin depender de JDBC.
 * 
 * - Principio SRP: Su única responsabilidad es gestionar la lista de pedidos en memoria.
 * - Principio ISP: Implementa únicamente el método guardar(Pedido) que requiere la interfaz.
 * - Principio DIP: Puede inyectarse en GestorPedidos a través de la abstracción PedidoRepository.
 */
public class PedidoRepositoryMemoria implements PedidoRepository {

    private final List<Pedido> baseDatosMemoria = new ArrayList<>();

    @Override
    public void guardar(Pedido pedido) {
        baseDatosMemoria.add(pedido);
        System.out.println("Pedido guardado en memoria correctamente (Total registrados: "
                + baseDatosMemoria.size() + ").");
    }
}

