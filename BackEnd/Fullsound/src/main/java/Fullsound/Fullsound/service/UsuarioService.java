package Fullsound.Fullsound.service;
import Fullsound.Fullsound.dto.request.UpdateUsuarioRequest;
import Fullsound.Fullsound.dto.response.UsuarioResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
public interface UsuarioService {
    UsuarioResponse getById(Integer id);
    UsuarioResponse getByNombreUsuario(String nombreUsuario);
    Integer getIdByNombreUsuario(String nombreUsuario);
    List<UsuarioResponse> getAll();
    Page<UsuarioResponse> getAll(Pageable pageable);
    UsuarioResponse updateProfile(String nombreUsuario, UpdateUsuarioRequest request);
    UsuarioResponse updateById(Integer id, UpdateUsuarioRequest request);
    void deactivate(Integer id);
    void activate(Integer id);
    void cambiarPassword(String nombreUsuario, String passwordActual, String passwordNueva);
}
