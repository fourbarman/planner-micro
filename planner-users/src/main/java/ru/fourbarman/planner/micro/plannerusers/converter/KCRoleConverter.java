package ru.fourbarman.planner.micro.plannerusers.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/*
    Takes JWT payload and converts it to spring security roles
 */
public class KCRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt value) {
        // получаем раздел json
        Map<String, Object> realmAccess = (Map<String, Object>) value.getClaims().get("realm_access");

        // если объект (раздел) пустой, значит нет ролей => вернуть пустой лист
        if (realmAccess == null || realmAccess.isEmpty()) {
            return new ArrayList<>();
        }

        // конвертация из текста в Authority
        Collection<GrantedAuthority> authorities = new ArrayList<>();

        for (String roleName : (List<String>)realmAccess.get("roles")) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + roleName));
        }

        return authorities;
    }
}
