package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.AuthResponseDTO;
import com.sistemareclamos.api.dtos.LoginRequestDTO;
import com.sistemareclamos.api.entities.Usuario;
import com.sistemareclamos.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponseDTO login(LoginRequestDTO request) {
        // 1. Buscar al usuario
        Usuario usuario = usuarioRepository.findByNombreUsuario(request.nombreUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 2. Verificar que la contraseña ingresada coincida con la encriptada
        if (!passwordEncoder.matches(request.contrasena(), usuario.getContrasena())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // 3. Generar el token
        String token = jwtService.generarToken(usuario);

        // 4. Armar la respuesta para Angular
        return new AuthResponseDTO(
                token,
                usuario.getNombreUsuario(),
                usuario.getRol().getIdRol(),
                usuario.getArea().getIdArea()
        );
    }
}