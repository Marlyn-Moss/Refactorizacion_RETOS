package persistencia;

import modelo.Pedido;

/**
 * Interfaz que define la operación de almacenamiento de pedidos.
 * 
 * - Principio SRP: Separa la responsabilidad de persistencia del resto del sistema.
 * - Principio ISP (Segregación de Interfaces): Es una interfaz pequeña y específica;
 *   define únicamente el método guardar(Pedido), sin obligar a las implementaciones
 *   a definir métodos de reportes, envíos o consultas que el servicio no necesita.
 * - Principio DIP (Inversión de Dependencias): Actúa como la abstracción de la cual
 *   depende GestorPedidos, evitando acoplarse a una base de datos concreta.
 */
public interface PedidoRepository {

    void guardar(Pedido pedido);
}

