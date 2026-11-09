# Laboratorio Java — Refactorización de `GestorPedidos` con SOLID (SRP, OCP, LSP)

Este proyecto contiene la solución completa del laboratorio de refactorización orientada a objetos en **Java estándar**, aplicando los tres primeros principios **SOLID**:

- **SRP (*Single Responsibility Principle*):** Principio de Responsabilidad Única.
- **OCP (*Open/Closed Principle*):** Principio Abierto/Cerrado.
- **LSP (*Liskov Substitution Principle*):** Principio de Sustitución de Liskov.

---

## Estructura del Proyecto

```text
Refactorizacion_RETOS/
├── .vscode/
│   └── settings.json
└── src/
    ├── Main.java
    ├── GestorPedidos.java
    │
    ├── modelo/
    │   └── Pedido.java
    │
    ├── validacion/
    │   └── ValidadorPedido.java
    │
    ├── calculo/
    │   ├── CalculadoraIVA.java
    │   └── CalculadoraTotal.java
    │
    ├── descuentos/
    │   ├── Descuento.java
    │   ├── DescuentoNormal.java
    │   ├── DescuentoVIP.java
    │   ├── DescuentoFrecuente.java
    │   └── DescuentoEstudiante.java
    │
    ├── persistencia/
    │   ├── PedidoRepository.java
    │   └── PedidoRepositoryMemoria.java
    │
    └── notificaciones/
        ├── Notificador.java
        └── NotificadorConsola.java
```

> **Nota técnica sobre el paquete `modelo`:** En Java estándar, las clases que pertenecen a un paquete con nombre (como `validacion` o `persistencia`) no pueden importar clases del paquete por defecto (*unnamed package*). Por ello, `Pedido.java` se ubicó en `src/modelo/Pedido.java` (`package modelo;`), garantizando que todo el proyecto compile limpiamente en cualquier IDE o desde la terminal.

---

## Paso 1 — Identificación de Responsabilidades en el Código Original (SRP)

En la versión original, `GestorPedidos` concentraba **6 responsabilidades distintas**:

1. **Validar que el pedido tenga productos:** `if (p.getItems().isEmpty()) throw new IllegalArgumentException("Pedido vacío");`
2. **Calcular el subtotal y aplicar descuentos según el tipo de cliente:** `if (tipo.equals("VIP")) ... else if (tipo.equals("FRECUENTE")) ...`
3. **Calcular y sumar el IVA del 13%:** `total += total * 0.13;`
4. **Guardar el pedido en una base de datos (JDBC):** `DriverManager.getConnection(URL)...`
5. **Enviar una notificación por correo al cliente:** `EmailSender.enviar(p.getCorreo(), "Pedido recibido");`
6. **Mostrar el resultado en la consola:** `System.out.println("Total: $" + total);`

### ¿Por qué esto viola SRP?
El **Principio de Responsabilidad Única (SRP)** establece que *una clase debe tener una única razón para cambiar*. Al concentrar estas 6 tareas en `GestorPedidos`, la clase tendría que modificarse si cambia la tasa del IVA, si se agrega un nuevo descuento, si se cambia de motor de base de datos, si cambia el servidor de correo o si cambian las reglas de validación, generando un alto acoplamiento y riesgo de introducir errores.

---

## Paso 2 — Separación de Responsabilidades (SRP)

Cada responsabilidad se delegó a una clase especializada:

| Responsabilidad | Clase / Interfaz | Razón única para cambiar |
| :--- | :--- | :--- |
| Datos del pedido | `modelo.Pedido` | Cambian los atributos propios de un pedido. |
| Validación | `validacion.ValidadorPedido` | Cambian las reglas de validación del pedido. |
| Cálculo de IVA (13%) | `calculo.CalculadoraIVA` | Cambia el porcentaje o fórmula del impuesto. |
| Cálculo del total | `calculo.CalculadoraTotal` | Cambia el orden o flujo matemático del total. |
| Reglas de descuento | `descuentos.Descuento` y sus clases | Cambian las políticas o porcentajes de descuento. |
| Persistencia | `persistencia.PedidoRepositoryMemoria` | Cambia el mecanismo de almacenamiento en memoria. |
| Notificación | `notificaciones.NotificadorConsola` | Cambia el formato o medio de notificación. |
| Coordinación | `GestorPedidos` | Cambia el flujo general de pasos al registrar un pedido. |

---

## Paso 3 — Implementación de Descuentos con OCP

### El problema del `if/else` original
En el código original:
```java
if (tipo.equals("VIP")) total *= 0.85;
else if (tipo.equals("FRECUENTE")) total *= 0.95;
```
Cada vez que el negocio requería un nuevo tipo de cliente (por ejemplo, `ESTUDIANTE`), era obligatorio abrir y modificar `GestorPedidos`, violando el **Principio Abierto/Cerrado (OCP)**.

### Solución con Polimorfismo
Se creó la interfaz `Descuento` con el método `aplicarDescuento(double subtotal)` y sus implementaciones:
- `DescuentoNormal`: paga el 100% (0% de descuento).
- `DescuentoVIP`: paga el 85% (15% de descuento).
- `DescuentoFrecuente`: paga el 95% (5% de descuento).
- `DescuentoEstudiante`: paga el 90% (10% de descuento).

---

## Paso 4 — Verificación del Principio de Sustitución de Liskov (LSP)

Todas las implementaciones de `Descuento` cumplen el mismo contrato:
- Reciben un `subtotal` válido y devuelven el monto tras el descuento.
- No lanzan excepciones inesperadas ni modifican el objeto `Pedido`.
- En `Main.java` se demuestra cómo una referencia `Descuento descuento = new DescuentoVIP();` puede sustituirse por `descuento = new DescuentoFrecuente();` sin alterar una sola línea de `GestorPedidos`.

---

## Paso 5 — Incorporación de `DescuentoEstudiante`

Para agregar el descuento del 10% para estudiantes:
- Se creó únicamente la clase `DescuentoEstudiante` implementando `Descuento`.
- **No se modificó** `GestorPedidos`, `Descuento`, `DescuentoNormal`, `DescuentoVIP` ni `DescuentoFrecuente`.

---

## Cómo Compilar y Ejecutar

### Opción 1: Desde Visual Studio Code
1. Abrir la carpeta `Refactorizacion_RETOS` en Visual Studio Code.
2. Abrir `src/Main.java`.
3. Hacer clic en **Run** sobre el método `main`.

### Opción 2: Desde la terminal con `javac` y `java`
```bash
javac -encoding UTF-8 -d bin src/Main.java src/GestorPedidos.java src/modelo/*.java src/validacion/*.java src/calculo/*.java src/descuentos/*.java src/persistencia/*.java src/notificaciones/*.java
java -cp bin Main
```

---

## Salida Esperada en Consola

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
Total de pedidos almacenados en memoria: 4
==============================================================
```

