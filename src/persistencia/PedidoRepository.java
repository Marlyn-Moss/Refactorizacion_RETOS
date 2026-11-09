package persistencia;

import java.util.List;
import modelo.Pedido;

/**
 * Interfaz que define las operaciones de almacenamiento de pedidos.
 * 
 * Principio SRP: Separa la lógica de persistencia del resto del sistema.
 * Principio OCP: Permite cambiar o agregar nuevas formas de guardar pedidos
 * (en memoria, archivo, base de datos) sin modificar GestorPedidos.
 */
public interface PedidoRepository {

    void guardar(Pedido pedido);

    List<Pedido> obtenerTodos();
}

