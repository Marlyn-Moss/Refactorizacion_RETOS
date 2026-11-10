# Laboratorio Java — Refactorización de `GestorPedidos` con los Cinco Principios SOLID

---

## A. Introducción

Este laboratorio de programación orientada a objetos en **Java estándar** consiste en refactorizar la clase `GestorPedidos`, la cual originalmente concentraba múltiples responsabilidades de negocio, persistencia y notificación en un único método.

El propósito del proyecto es transformar un diseño monolítico y rígido en una arquitectura limpia, modular y mantenible aplicando los **cinco principios SOLID**:

- **SRP (*Single Responsibility Principle*):** Principio de Responsabilidad Única.
- **OCP (*Open/Closed Principle*):** Principio Abierto/Cerrado.
- **LSP (*Liskov Substitution Principle*):** Principio de Sustitución de Liskov.
- **ISP (*Interface Segregation Principle*):** Principio de Segregación de Interfaces.
- **DIP (*Dependency Inversion Principle*):** Principio de Inversión de Dependencias.

Adicionalmente, el laboratorio analiza los niveles de **cohesión** y **acoplamiento** resultantes en cada componente del sistema.

---

## B. Código Original y Problemas de Diseño

### Código original antes de la refactorización

```java
public class GestorPedidos {
    public void registrar(Pedido p, String tipo) throws Exception {
        if (p.getItems().isEmpty())
            throw new IllegalArgumentException("Pedido vacío");

        double total = p.calcularSubtotal();

        if (tipo.equals("VIP")) total *= 0.85;
        else if (tipo.equals("FRECUENTE")) total *= 0.95;

        total += total * 0.13; // IVA

        try (Connection c = DriverManager.getConnection(URL)) {
            c.prepareStatement("INSERT INTO pedidos ...")
                .executeUpdate();
        }

        EmailSender.enviar(p.getCorreo(), "Pedido recibido");

        System.out.println("Total: $" + total);
    }
}
```

### Principales problemas de diseño

1. **Violación de SRP (Baja cohesión):** `GestorPedidos` realizaba seis tareas distintas en un solo método: validar el pedido, aplicar descuentos, calcular el IVA del 13%, conectarse mediante JDBC para insertar en una base de datos, enviar un correo electrónico y mostrar el total en consola. Cualquier cambio en cualquiera de estas áreas obligaba a modificar la misma clase.
2. **Violación de OCP:** Los descuentos dependían de estructuras condicionales (`if / else if`) basadas en cadenas de texto (`"VIP"`, `"FRECUENTE"`). Agregar un nuevo tipo de cliente (como `"ESTUDIANTE"`) obligaba a modificar el código fuente de `GestorPedidos`.
3. **Ausencia de abstracciones para LSP e ISP:** Al no existir interfaces para los descuentos, el almacenamiento o las notificaciones, era imposible sustituir comportamientos o aislar contratos específicos.
4. **Violación de DIP (Alto acoplamiento):** La clase dependía directamente de detalles de implementación concretos (`DriverManager` de JDBC y `EmailSender`), impidiendo probar el sistema sin una base de datos o un servidor de correo activos.

---

## C. Aplicación de los Cinco Principios SOLID

### 1. SRP — Principio de Responsabilidad Única (*Single Responsibility Principle*)
Cada responsabilidad identificada en el código original fue extraída a una clase especializada con una única razón para cambiar:
- **`Pedido` (`modelo`):** Almacena y expone los datos del pedido (correo, lista de productos y subtotal).
- **`ValidadorPedido` (`validacion`):** Verifica que el pedido no sea nulo, contenga productos, tenga un subtotal mayor que cero y posea un correo válido.
- **`CalculadoraIVA` (`calculo`):** Calcula el impuesto del 13% sobre un monto base.
- **`CalculadoraTotal` (`calculo`):** Coordina la operación aritmética aplicando primero el descuento al subtotal y posteriormente sumando el IVA.
- **`PedidoRepositoryMemoria` (`persistencia`):** Guarda los pedidos en memoria utilizando un `ArrayList`.
- **`NotificadorConsola` (`notificaciones`):** Simula el envío de la notificación mostrándola en consola.
- **`GestorPedidos`:** Actúa exclusivamente como coordinador del proceso de registro, delegando cada paso a los componentes anteriores.

### 2. OCP — Principio Abierto/Cerrado (*Open/Closed Principle*)
Se eliminaron los bloques `if / else` de descuentos y se definió la interfaz `Descuento` con el método `aplicarDescuento(double subtotal)`. Cada política de descuento se implementó en su propia clase:
- **`DescuentoNormal`:** 0% de descuento (el cliente paga el 100%).
- **`DescuentoVIP`:** 15% de descuento (el cliente paga el 85%).
- **`DescuentoFrecuente`:** 5% de descuento (el cliente paga el 95%).
- **`DescuentoEstudiante`:** 10% de descuento (el cliente paga el 90%).

Cuando se incorporó el cliente **ESTUDIANTE**, únicamente se creó la clase `DescuentoEstudiante` sin modificar `GestorPedidos`, `Descuento` ni los descuentos existentes.

### 3. LSP — Principio de Sustitución de Liskov (*Liskov Substitution Principle*)
Todas las clases que implementan `Descuento` cumplen estrictamente el mismo contrato:
- Reciben un `subtotal` numérico válido y retornan el monto resultante tras el descuento.
- No lanzan excepciones inesperadas ni alteran el estado del objeto `Pedido`.
- Gracias a ello, `GestorPedidos` y `CalculadoraTotal` pueden recibir una instancia de `DescuentoNormal`, `DescuentoVIP`, `DescuentoFrecuente` o `DescuentoEstudiante` de forma completamente intercambiable sin que el sistema falle ni requiera verificaciones de tipo.

### 4. ISP — Principio de Segregación de Interfaces (*Interface Segregation Principle*)
En lugar de crear una interfaz generalista (por ejemplo, `ServicioGeneral` con métodos de persistencia, correo, descuentos y reportes), se diseñaron tres interfaces pequeñas, cohesivas y específicas:
- **`Descuento`:** Define únicamente `double aplicarDescuento(double subtotal);`.
- **`PedidoRepository`:** Define únicamente `void guardar(Pedido pedido);`.
- **`Notificador`:** Define únicamente `void enviar(String destinatario, String mensaje);`.

De esta manera, ninguna clase se ve obligada a implementar métodos que no utiliza.

### 5. DIP — Principio de Inversión de Dependencias (*Dependency Inversion Principle*)
`GestorPedidos` no instancia sus dependencias utilizando `new` dentro de su código, ni conoce a `PedidoRepositoryMemoria` o `NotificadorConsola`. En su lugar:
1. Declara atributos privados basados en las interfaces `PedidoRepository` y `Notificador` (junto con los componentes de validación y cálculo).
2. Recibe dichas dependencias a través de su **constructor** (inyección de dependencias).
3. `Main.java` actúa como ensamblador (*Composition Root*), instanciando las clases concretas y entregándolas al constructor de `GestorPedidos`. Si en el futuro se requiere guardar en MySQL o enviar correos reales, solo se cambia la instancia creada en `Main.java` sin tocar `GestorPedidos`.

---

## D. Análisis de Cohesión

La **cohesión** mide el grado en que los elementos internos de una clase pertenecen juntos y colaboran para cumplir un único propósito. En el diseño refactorizado predomina la **cohesión funcional** (el nivel más alto y deseable) en las clases especializadas, y una combinación de **cohesión funcional y secuencial** en las clases coordinadoras:

| Clase | Responsabilidad | Tipo de cohesión | Justificación |
| :--- | :--- | :--- | :--- |
| `Pedido` | Representar los datos de un pedido (correo, productos y subtotal). | **Funcional / Comunicacional** | Todos sus atributos y métodos de acceso operan sobre la misma estructura de datos que define a la entidad pedido. |
| `ValidadorPedido` | Verificar que un pedido cumpla las reglas de negocio antes de procesarse. | **Funcional** | Su único método `validar(Pedido)` agrupa comprobaciones que contribuyen exclusivamente a una tarea: determinar la validez del pedido. |
| `CalculadoraIVA` | Calcular el monto del IVA (13%) y sumarlo a un monto base. | **Funcional** | Todas sus operaciones y su constante `TASA_IVA` están dedicadas a una única función matemática bien definida: el cálculo del impuesto. |
| `CalculadoraTotal` | Obtener el total final aplicando el descuento y sumando el IVA. | **Secuencial / Funcional** | Es funcional porque resuelve una única tarea (calcular el total a pagar) y secuencial porque la salida del descuento (`montoConDescuento`) es la entrada directa para el cálculo del IVA. |
| `DescuentoNormal`, `DescuentoVIP`, `DescuentoFrecuente`, `DescuentoEstudiante` | Aplicar el factor de descuento correspondiente al tipo de cliente sobre el subtotal. | **Funcional** | Cada clase realiza exactamente una operación atómica (`aplicarDescuento`) sin efectos secundarios ni responsabilidades adicionales. |
| `PedidoRepositoryMemoria` | Almacenar pedidos en una lista en memoria (`ArrayList`). | **Funcional** | Su atributo `baseDatosMemoria` y su método `guardar(Pedido)` colaboran exclusivamente para persistir el pedido en memoria. |
| `NotificadorConsola` | Simular el envío de una notificación mostrándola en consola. | **Funcional** | Su método `enviar(String, String)` realiza una única acción puntual de salida de notificación al cliente. |
| `GestorPedidos` | Coordinar el flujo completo de registro de un pedido. | **Secuencial / Funcional** | A nivel de negocio tiene un propósito único (registrar el pedido), y internamente organiza una secuencia ordenada de pasos (validar → calcular total → guardar → notificar → mostrar total) donde cada etapa habilita la siguiente. |

---

## E. Análisis de Acoplamiento

El **acoplamiento** mide el grado de interdependencia entre distintos módulos o clases. El diseño evita los acoplamientos más perjudiciales (**de contenido**, **común/global** y **de control**) al encapsular todos los atributos como `private final`, no utilizar variables globales mutables y eliminar los condicionales por tipo de cliente (`String tipo`).

En la siguiente tabla se distingue tanto el **tipo de información intercambiada** (datos primitivos vs. objetos de dominio) como la **dependencia estructural** (clases concretas vs. abstracciones mediante interfaces):

| Clase o relación | Tipo de acoplamiento | Nivel de dependencia | Justificación |
| :--- | :--- | :--- | :--- |
| `CalculadoraTotal` → `CalculadoraIVA` | **Acoplamiento de datos** | **Bajo** | `CalculadoraTotal` invoca `agregarIVA(double montoBase)` pasando únicamente un valor primitivo (`double`), que es el dato exacto necesario para calcular el impuesto. |
| `CalculadoraTotal` → `Descuento` | **Acoplamiento de datos + Abstracción (Polimórfico)** | **Muy bajo** | Se intercambia un único dato primitivo (`double subtotal`) a través de la interfaz `Descuento`. `CalculadoraTotal` desconoce qué clase concreta de descuento se está ejecutando. |
| `GestorPedidos` → `Notificador` | **Acoplamiento de datos + Inyección de abstracción (DIP)** | **Muy bajo** | `GestorPedidos` depende de la interfaz `Notificador` (recibida por constructor) y al invocar `enviar(pedido.getCorreo(), "...")` solo pasa dos cadenas (`String`) en lugar de entregar todo el objeto `Pedido`. |
| `GestorPedidos` → `PedidoRepository` | **Acoplamiento por estampado (*Stamp*) + Inyección de abstracción (DIP)** | **Bajo** | Se pasa la referencia al objeto `Pedido` porque el repositorio necesita almacenar la entidad completa. El acoplamiento estructural se mantiene bajo porque `GestorPedidos` depende de la interfaz `PedidoRepository` y no de `PedidoRepositoryMemoria`. |
| `GestorPedidos` → `ValidadorPedido` | **Acoplamiento por estampado (*Stamp*)** | **Bajo** | `ValidadorPedido.validar(Pedido)` recibe el objeto `Pedido` ya que necesita inspeccionar varias de sus propiedades (`items`, `subtotal`, `correo`) para validarlo integralmente, sin modificar su estado interno. |
| `GestorPedidos` → `CalculadoraTotal` | **Acoplamiento de datos (con interfaz `Descuento`)** | **Bajo** | `GestorPedidos` extrae `pedido.calcularSubtotal()` (un `double`) y lo pasa junto con la abstracción `Descuento` a `CalculadoraTotal`, evitando que la calculadora dependa de la clase `Pedido`. |
| `Main` → Clases concretas | **Ensamblaje / Composición (*Composition Root*)** | **Localizado en el punto de entrada** | `Main` es el único lugar que conoce las clases concretas (`PedidoRepositoryMemoria`, `NotificadorConsola`, `DescuentoVIP`, etc.) con el fin de instanciarlas e inyectarlas en `GestorPedidos`, aislando al resto del sistema de dichas dependencias. |

---

## F. Comparación: Código Original vs. Código Refactorizado

| Criterio | Código Original | Código Refactorizado con SOLID |
| :--- | :--- | :--- |
| **Responsabilidades (SRP)** | Una sola clase (`GestorPedidos`) validaba, calculaba descuentos e IVA, guardaba en BD, enviaba correos e imprimía en consola. | Cada responsabilidad reside en su propia clase especializada; `GestorPedidos` solo coordina el flujo. |
| **Extensibilidad de descuentos (OCP)** | Basado en `if / else if` sobre un `String tipo` (acoplamiento de control). Agregar `ESTUDIANTE` obligaba a modificar `GestorPedidos`. | Basado en polimorfismo con la interfaz `Descuento`. `DescuentoEstudiante` se agregó sin modificar ninguna clase existente. |
| **Sustitución (LSP)** | No existía jerarquía ni contrato común para los descuentos. | Todas las implementaciones de `Descuento` son sustituibles entre sí sin alterar el comportamiento de `GestorPedidos`. |
| **Diseño de interfaces (ISP)** | No utilizaba interfaces; dependía directamente de utilidades externas. | Utiliza tres interfaces pequeñas y específicas (`Descuento`, `PedidoRepository`, `Notificador`), cada una con un único método. |
| **Gestión de dependencias (DIP)** | Acoplado directamente a `DriverManager` (JDBC) y `EmailSender` dentro del método `registrar`. | `GestorPedidos` depende de interfaces y recibe `PedidoRepository` y `Notificador` mediante inyección por constructor. |
| **Cohesión y Acoplamiento** | Baja cohesión (múltiples razones de cambio) y alto acoplamiento externo y de control. | Alta cohesión funcional en cada componente y bajo acoplamiento basado en datos e interfaces. |

---

## G. Conclusión

Las decisiones de diseño aplicadas durante este laboratorio permitieron transformar una clase rígida y difícil de mantener en un sistema modular alineado con los cinco principios SOLID. La creación de clases especializadas (`ValidadorPedido`, `CalculadoraIVA`, `CalculadoraTotal`, `PedidoRepositoryMemoria` y `NotificadorConsola`) elevó la cohesión del proyecto a un nivel funcional, asegurando que cada componente tenga una única responsabilidad bien definida y una sola razón para cambiar. Al mismo tiempo, la definición de interfaces pequeñas y específicas (`Descuento`, `PedidoRepository` y `Notificador`) junto con la inyección de dependencias por constructor en `GestorPedidos` redujeron drásticamente el acoplamiento entre módulos: el coordinador principal dejó de depender de implementaciones concretas y de estructuras condicionales, permitiendo incorporar nuevos tipos de descuento como `DescuentoEstudiante` o cambiar el medio de almacenamiento y notificación sin modificar la lógica central del negocio.

---

## Cómo Compilar y Ejecutar el Proyecto

### Opción 1: Desde Visual Studio Code
1. Abrir la carpeta `Refactorizacion_RETOS` en Visual Studio Code.
2. Abrir el archivo `src/Main.java`.
3. Hacer clic en **Run** sobre el método `main`.

### Opción 2: Desde la terminal con `javac` y `java`
```bash
javac -encoding UTF-8 -d bin src/Main.java src/GestorPedidos.java src/modelo/*.java src/validacion/*.java src/calculo/*.java src/descuentos/*.java src/persistencia/*.java src/notificaciones/*.java
java -cp bin Main
```

### Salida en Consola de los 4 Casos de Prueba

```text
==============================================================
   LABORATORIO JAVA - REFACTORIZACIÓN DE GESTORPEDIDOS (SOLID)
==============================================================

--- CASO 1: CLIENTE NORMAL ---
Subtotal: $100.00 | Descuento: 0% | IVA: 13%
Pedido guardado en memoria correctamente (Total registrados: 1).
Notificación enviada a: cliente.normal@gmail.com
Mensaje: Pedido recibido correctamente.
Total: $113.00

--- CASO 2: CLIENTE VIP ---
Subtotal: $100.00 | Descuento: 15% | IVA: 13%
Pedido guardado en memoria correctamente (Total registrados: 2).
Notificación enviada a: cliente.vip@gmail.com
Mensaje: Pedido recibido correctamente.
Total: $96.05

--- CASO 3: CLIENTE FRECUENTE ---
Subtotal: $100.00 | Descuento: 5% | IVA: 13%
Pedido guardado en memoria correctamente (Total registrados: 3).
Notificación enviada a: cliente.frecuente@gmail.com
Mensaje: Pedido recibido correctamente.
Total: $107.35

--- CASO 4: CLIENTE ESTUDIANTE ---
Subtotal: $100.00 | Descuento: 10% | IVA: 13%
Pedido guardado en memoria correctamente (Total registrados: 4).
Notificación enviada a: estudiante@gmail.com
Mensaje: Pedido recibido correctamente.
Total: $101.70

==============================================================
Ejecución completada aplicando los 5 principios SOLID.
==============================================================
```
