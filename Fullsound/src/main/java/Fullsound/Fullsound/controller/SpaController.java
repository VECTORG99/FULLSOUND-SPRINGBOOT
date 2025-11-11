package Fullsound.Fullsound.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para manejar las rutas de la SPA
 * Redirige todas las rutas que no sean /api/* a index.html
 * para que React Router maneje la navegación
 */
@Controller
public class SpaController {

    /**
     * Redirige todas las rutas no-API a index.html
     * Esto permite que React Router maneje las rutas del frontend
     * sin recibir 404 del servidor
     * 
     * @return forward a index.html
     */
    @GetMapping(value = {"/{path:[^\\.]*}", "/{path1}/{path2:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}
