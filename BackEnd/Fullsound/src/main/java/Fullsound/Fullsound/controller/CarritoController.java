package Fullsound.Fullsound.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para gestión del carrito de compras
 *
 * @deprecated La funcionalidad de carrito no está implementada en el backend.
 * El carrito se maneja en el frontend (localStorage). Todos los endpoints
 * devuelven 410 Gone indicando que la funcionalidad no está implementada.
 */
@Deprecated
@RestController
@RequestMapping("/carrito")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CarritoController {

    private static final String NOT_IMPLEMENTED_MSG = "Funcionalidad no implementada";

    private ResponseEntity<Map<String, Object>> gone() {
        Map<String, Object> body = new HashMap<>();
        body.put("message", NOT_IMPLEMENTED_MSG);
        body.put("success", false);
        return ResponseEntity.status(HttpStatus.GONE).body(body);
    }

    /**
     * @deprecated Funcionalidad no implementada
     */
    @Deprecated
    @GetMapping
    public ResponseEntity<Map<String, Object>> obtenerCarrito() {
        return gone();
    }

    /**
     * @deprecated Funcionalidad no implementada
     */
    @Deprecated
    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> agregarItem(@RequestBody Map<String, Object> item) {
        return gone();
    }

    /**
     * @deprecated Funcionalidad no implementada
     */
    @Deprecated
    @PutMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> actualizarItem(
            @PathVariable String itemId,
            @RequestBody Map<String, Object> data) {
        return gone();
    }

    /**
     * @deprecated Funcionalidad no implementada
     */
    @Deprecated
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Map<String, Object>> eliminarItem(@PathVariable String itemId) {
        return gone();
    }

    /**
     * @deprecated Funcionalidad no implementada
     */
    @Deprecated
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> vaciarCarrito() {
        return gone();
    }

    /**
     * @deprecated Funcionalidad no implementada
     */
    @Deprecated
    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(@RequestBody Map<String, Object> datosCompra) {
        return gone();
    }
}
