package com.artivisi.accountingfinance.service;

import com.artivisi.accountingfinance.entity.User;
import com.artivisi.accountingfinance.enums.Role;
import com.artivisi.accountingfinance.repository.UserRepository;
import com.artivisi.accountingfinance.security.Permission;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!user.getActive()) {
            throw new UsernameNotFoundException("User is inactive: " + username);
        }

        List<GrantedAuthority> authorities = buildAuthorities(user.getRoles());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getActive(),
                true,
                true,
                true,
                authorities
        );
    }

    /**
     * Build authorities from roles.
     * Includes both ROLE_xxx and permission-based authorities.
     */
    private List<GrantedAuthority> buildAuthorities(Set<Role> roles) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        // Add role-based authorities (ROLE_ADMIN, ROLE_OWNER, etc.)
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }

        // Add permission-based authorities
        Set<String> permissions = Permission.getPermissionsForRoles(roles);
        for (String permission : permissions) {
            authorities.add(new SimpleGrantedAuthority(permission));
        }

        return authorities;
    }
}
