package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.UpdateUsuarioRequest;
import Fullsound.Fullsound.dto.response.UsuarioResponse;
import java.util.List;
public interface UsuarioService {
    UsuarioResponse getById(Integer id);
    UsuarioResponse getByNombreUsuario(String nombreUsuario);
    List<UsuarioResponse> getAll();
    UsuarioResponse updateProfile(String nombreUsuario, UpdateUsuarioRequest request);
    UsuarioResponse updateById(Integer id, UpdateUsuarioRequest request);
    void deactivate(Integer id);
    void activate(Integer id);
    void cambiarPassword(String nombreUsuario, String passwordActual, String passwordNueva);
}
