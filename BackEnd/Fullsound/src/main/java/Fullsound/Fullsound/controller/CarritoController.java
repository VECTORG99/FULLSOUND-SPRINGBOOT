package Fullsound.Fullsound.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para gestión del carrito de compras
 * El carrito se maneja principalmente en el frontend (localStorage)
 * Este controlador proporciona endpoints básicos para compatibilidad
 */
@RestController
@RequestMapping("/carrito")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CarritoController {

    /**
     * Obtiene el carrito vacío
     * El frontend maneja el carrito en localStorage
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerCarrito() {
        Map<String, Object> carrito = new HashMap<>();
        carrito.put("items", new ArrayList<>());
        carrito.put("total", 0);
        carrito.put("source", "backend");
        return ResponseEntity.ok(carrito);
    }

    /**
     * Endpoint para agregar items (no implementado, se usa localStorage)
     */
    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> agregarItem(@RequestBody Map<String, Object> item) {
        Map<String, Object> carrito = new HashMap<>();
        carrito.put("items", new ArrayList<>());
        carrito.put("total", 0);
        carrito.put("message", "Item agregado (manejado en frontend)");
        return ResponseEntity.ok(carrito);
    }

    /**
     * Endpoint para actualizar items (no implementado, se usa localStorage)
     */
    @PutMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> actualizarItem(
            @PathVariable String itemId,
            @RequestBody Map<String, Object> data) {
        Map<String, Object> carrito = new HashMap<>();
        carrito.put("items", new ArrayList<>());
        carrito.put("total", 0);
        carrito.put("message", "Item actualizado (manejado en frontend)");
        return ResponseEntity.ok(carrito);
    }

    /**
     * Endpoint para eliminar items (no implementado, se usa localStorage)
     */
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> eliminarItem(@PathVariable String itemId) {
        Map<String, Object> carrito = new HashMap<>();
        carrito.put("items", new ArrayList<>());
        carrito.put("total", 0);
        carrito.put("message", "Item eliminado (manejado en frontend)");
        return ResponseEntity.ok(carrito);
    }

    /**
     * Endpoint para vaciar el carrito (no implementado, se usa localStorage)
     */
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> vaciarCarrito() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Carrito vaciado (manejado en frontend)");
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint para checkout (no implementado, se usa localStorage)
     */
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody Map<String, Object> datosCompra) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Checkout procesado (manejado en frontend)");
        response.put("success", true);
        response.put("orderId", System.currentTimeMillis());
        return ResponseEntity.ok(response);
    }
}
