package com.proyecto.blog.service;

import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.repository.IUserSecRepository;
import com.proyecto.blog.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserDetailsServiceImp {

    @Autowired
    private IUserSecRepository userRepo;
    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException{

        //Obtengo un UserSec y debo devolverlo en formato UserDetails
        //Obtengo al usuario de nuestra BD
        UserSec userSec = userRepo.findUserEntityByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario" + username + " no fué encontrado"));

        //Creo una lista para los permisos
        List<SimpleGrantedAuthority> authorityList= new ArrayList<>();

        //Obtengo los roles y los convierto en SimpleGrantedAuthority

        //a los roles y los convierto en SimpleGrantedAuthority para poder agregarlos a la authorityList
        userSec.getRolesList()
                .forEach(role -> authorityList.add(new SimpleGrantedAuthority("ROLE_".concat(role.getRole()))));

        //agrego los permisos
        userSec.getRolesList().stream()
                .flatMap(role -> role.getPermissionsList().stream()) //acá recorro los permisos de los roles
                .forEach(permission -> authorityList.add(new SimpleGrantedAuthority(permission.getPermissionName())));

        //retornamos el usuario en formato Spring Security con los datos de nuestro userSec
        return new User(userSec.getUsername(),
                userSec.getPassword(),
                userSec.isEnabled(),
                userSec.isAccountNotExpired(),
                userSec.isCredentialNotExpired(),
                userSec.isAccountNotLocked(),
                authorityList);
    }

}
