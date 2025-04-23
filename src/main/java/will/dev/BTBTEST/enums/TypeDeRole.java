package will.dev.BTBTEST.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static will.dev.BTBTEST.enums.TypePermission.*;

@AllArgsConstructor
public enum TypeDeRole {
    ADMIN(Set.of(
            ADMIN_CREATE,
            ADMIN_READ,
            ADMIN_UPDATE,
            ADMIN_DELETE,

            MANAGER_CREATE,
            MANAGER_READ,
            MANAGER_UPDATE,
            MANAGER_DELETE
    )),
    MANAGER(Set.of(
            MANAGER_CREATE,
            MANAGER_READ,
            MANAGER_UPDATE,
            MANAGER_DELETE
    )),
    USER(Set.of(
            USER_CREATE,
            USER_READ
    ));

    @Getter
    Set<TypePermission> permission;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> grantedAuthorities = this.getPermission().stream().map(permission ->
                new SimpleGrantedAuthority(permission.name())).collect(Collectors.toList());

        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_"+this.name()));

        return grantedAuthorities;
    }
}
