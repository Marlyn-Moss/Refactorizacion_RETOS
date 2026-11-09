package persistencia;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import modelo.Pedido;

/**
 * Implementación de PedidoRepository que almacena los pedidos en memoria
 * utilizando un ArrayList, simulando una base de datos sin depender de JDBC.
 * 
 * Principio SRP: Su única responsabilidad es gestionar la lista de pedidos en memoria.
 */
public class PedidoRepositoryMemoria implements PedidoRepository {

    private final List<Pedido> baseDatosMemoria = new ArrayList<>();

    @Override
    public void guardar(Pedido pedido) {
        baseDatosMemoria.add(pedido);
        System.out.println("Pedido guardado en memoria correctamente (Total registrados: "
                + baseDatosMemoria.size() + ").");
    }

    @Override
    public List<Pedido> obtenerTodos() {
        return Collections.unmodifiableList(baseDatosMemoria);
    }
}

