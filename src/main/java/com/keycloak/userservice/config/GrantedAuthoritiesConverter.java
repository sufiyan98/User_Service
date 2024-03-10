package com.keycloak.userservice.config;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;


public class GrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        Map<String, Object> realmAccess = source.getClaimAsMap("realm_access");
        if (Objects.nonNull(realmAccess)) {
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            if (Objects.nonNull(roles)) {
                Collection<GrantedAuthority> list=new ArrayList<>();
                for(String role: roles) {
                    list.add(new SimpleGrantedAuthority("ROLE_" + role));
                }
                return list;
            }
        }
        return List.of();
    }
}