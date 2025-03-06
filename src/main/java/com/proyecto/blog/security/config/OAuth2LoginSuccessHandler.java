package com.proyecto.blog.security.config;


import com.proyecto.blog.model.Role;
import com.proyecto.blog.model.UserSec;
import com.proyecto.blog.repository.IUserSecRepository;
import com.proyecto.blog.service.UserService;
import com.proyecto.blog.utils.JwtUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

package com.tuempresa.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;


@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtUtils jwtUtils;  // Utilidad para generar el JWT

    @Autowired
    private IUserSecRepository userRepo;  // El repositorio para consultar los usuarios

    @Autowired
    private PasswordEncoder passwordEncoder;  // El codificador de contraseñas

    @Autowired
    private AuthenticationManager authenticationManager;  // Para autenticar al usuario después de crear o cargar

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Obtener los datos del usuario de GitHub/Google
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String username = oAuth2User.getAttribute("login");  // Usamos "login" para el username en GitHub, ajusta para otros proveedores
        String name = oAuth2User.getAttribute("name");

        if (username == null) {
            // Si no se encuentra el username (por ejemplo, en GitHub), se puede asignar un nombre temporal
            username = "oauth2_" + oAuth2User.getAttribute("id") + "_user";
        }

        // Buscar al usuario en la base de datos
        UserSec userSec = userRepo.findUserEntityByUsername(username).orElse(null);

        if (userSec == null) {
            // Si no existe, crear un nuevo usuario
            userSec = new UserSec();
            userSec.setUsername(username);  // Usamos el username de OAuth2
            userSec.setPassword(passwordEncoder.encode("defaultPassword"));  // Puedes usar una contraseña temporal o generar un hash adecuado

            Role role = new Role();  // Usamos el constructor sin parámetros
            role.setRole("USER");    // Asignamos el nombre del rol con el setter

            userSec.setRolesList(new HashSet<>(Arrays.asList(role)));  // Si el constructor acepta un id y un nombre de rol
            // Crear un Set<Role> a partir de una lista de roles
            // Asigna un rol por defecto
            userSec.setUsername(name);
            userRepo.save(userSec);  // Guarda el usuario nuevo
        }

        // Ahora se maneja la autenticación de la misma manera que con usuario y contraseña
        // Aquí usamos el nombre de usuario y contraseña generados para autenticar al usuario
        String password = "defaultPassword";  // La contraseña predeterminada generada para OAuth2
        Authentication auth = authenticate(userSec.getUsername(), password);

        // Si todo está correcto, generar JWT
        String token = jwtUtils.createToken(auth);

        // Enviar el token en la respuesta
        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
    }

    private Authentication authenticate(String username, String password) {
        // Con esto buscamos al usuario y autenticamos con la contraseña
        UserDetails userDetails = new User(username, password, new ArrayList<>());

        if (userDetails == null) {
            throw new BadCredentialsException("Invalid username or password");
        }

        return authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password, userDetails.getAuthorities())
        );
    }
}


