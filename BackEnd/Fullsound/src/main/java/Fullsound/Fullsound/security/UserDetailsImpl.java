package Fullsound.Fullsound.security;

import Fullsound.Fullsound.model.Rol;
import Fullsound.Fullsound.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de UserDetails para Spring Security.
 * 
 * @author VECTORG99
 * @version 1.0.0
 * @since 2025-11-13
 */
@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    
    private Integer id;
    private String nombreUsuario;
    private String correo;
    private String contraseña;
    private Boolean activo;
    private Collection<? extends GrantedAuthority> authorities;
    
    /**
     * Construye un UserDetailsImpl desde una entidad Usuario.
     */
    public static UserDetailsImpl build(Usuario usuario) {
        List<GrantedAuthority> authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getTipo()))
                .collect(Collectors.toList());
        
        return new UserDetailsImpl(
                usuario.getId(),
                usuario.getNombreUsuario(),
                usuario.getCorreo(),
                usuario.getContraseña(),
                usuario.getActivo(),
                authorities
        );
    }
    
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return contraseña;
    }
    
    @Override
    public String getUsername() {
        return nombreUsuario;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return activo;
    }
}
