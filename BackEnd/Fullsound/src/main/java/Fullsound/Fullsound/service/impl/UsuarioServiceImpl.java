package Fullsound.Fullsound.service.impl;

import Fullsound.Fullsound.dto.request.UpdateUsuarioRequest;
import Fullsound.Fullsound.dto.response.UsuarioResponse;
import Fullsound.Fullsound.exception.BadRequestException;
import Fullsound.Fullsound.exception.ResourceNotFoundException;
import Fullsound.Fullsound.mapper.UsuarioMapper;
import Fullsound.Fullsound.model.Usuario;
import Fullsound.Fullsound.repository.UsuarioRepository;
import Fullsound.Fullsound.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de usuarios.
 */
@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse getById(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id.toString()));
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse getByNombreUsuario(String nombreUsuario) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "nombreUsuario", nombreUsuario));
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> getAll() {
        List<Usuario> usuarios = usuarioRepository.findAll();
        return usuarios.stream()
                .map(usuarioMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UsuarioResponse updateProfile(String nombreUsuario, UpdateUsuarioRequest request) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "nombreUsuario", nombreUsuario));

        // Actualizar correo si se proporciona
        if (request.getCorreo() != null && !request.getCorreo().isEmpty()) {
            // Verificar que el correo no esté en uso por otro usuario
            if (usuarioRepository.existsByCorreo(request.getCorreo()) && 
                !usuario.getCorreo().equals(request.getCorreo())) {
                throw new BadRequestException("El correo ya está en uso");
            }
            usuario.setCorreo(request.getCorreo());
        }

        // Actualizar contraseña si se proporciona
        if (request.getContraseña() != null && !request.getContraseña().isEmpty()) {
            usuario.setContraseña(passwordEncoder.encode(request.getContraseña()));
        }

        Usuario usuarioActualizado = usuarioRepository.save(usuario);
        return usuarioMapper.toResponse(usuarioActualizado);
    }

    @Override
    @Transactional
    public void deactivate(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id.toString()));
        
        usuario.setActivo(false);
        usuarioRepository.save(usuario);
    }

    @Override
    @Transactional
    public void activate(Integer id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "id", id.toString()));
        
        usuario.setActivo(true);
        usuarioRepository.save(usuario);
    }
    
    @Override
    @Transactional
    public void cambiarPassword(String nombreUsuario, String passwordActual, String passwordNueva) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(nombreUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", "nombreUsuario", nombreUsuario));
        
        // Verificar que la contraseña actual sea correcta
        if (!passwordEncoder.matches(passwordActual, usuario.getContraseña())) {
            throw new BadRequestException("La contraseña actual es incorrecta");
        }
        
        // Actualizar con la nueva contraseña
        usuario.setContraseña(passwordEncoder.encode(passwordNueva));
        usuarioRepository.save(usuario);
    }
}
