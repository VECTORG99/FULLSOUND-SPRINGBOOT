package Fullsound.Fullsound.security;
import Fullsound.Fullsound.model.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
@Data
@AllArgsConstructor
public class UserDetailsImpl implements UserDetails {
    private Integer id;
    private String nombreUsuario;
    private String correo;
    private String contraseña;
    private Boolean activo;
    private Collection<? extends GrantedAuthority> authorities;
    public static UserDetailsImpl build(Usuario usuario) {
        List<GrantedAuthority> authorities = new java.util.ArrayList<>();
        if (usuario.getRol() != null) {
            authorities.add(new SimpleGrantedAuthority(usuario.getRol().getTipo()));
        }
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
