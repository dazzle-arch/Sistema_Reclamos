package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.AuthResponseDTO;
import com.sistemareclamos.api.dtos.LoginRequestDTO;
import com.sistemareclamos.api.entities.Usuario;
import com.sistemareclamos.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sistemareclamos.api.dtos.AuthResponseDTO;
import com.sistemareclamos.api.dtos.LoginRequestDTO;
import com.sistemareclamos.api.entities.Usuario;
import com.sistemareclamos.api.exception.AutenticacionException;
import com.sistemareclamos.api.exception.CuentaBloqueadaException;
import com.sistemareclamos.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String MSG_CREDENCIALES = "Usuario o contraseña incorrectos";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final IntentosLoginService intentosLoginService;

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO request) {
        Usuario usuario = usuarioRepository.findByNombreUsuario(request.nombreUsuario())
                .orElseThrow(() -> new AutenticacionException(MSG_CREDENCIALES));

        if (usuario.getBloqueadoHasta() != null && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            throw new CuentaBloqueadaException("Cuenta bloqueada temporalmente. Intenta más tarde");
        }

        if (!passwordEncoder.matches(request.contrasena(), usuario.getContrasena())) {
            intentosLoginService.registrarFallo(usuario.getIdUsuario()); // REQUIRES_NEW
            throw new AutenticacionException(MSG_CREDENCIALES);
        }

        if (!Boolean.TRUE.equals(usuario.getEstado())) {
            throw new AutenticacionException(MSG_CREDENCIALES);
        }

        usuario.setIntentosFallidos(0);
        usuario.setBloqueadoHasta(null);
        usuarioRepository.save(usuario);

        return new AuthResponseDTO(
                jwtService.generarToken(usuario),
                usuario.getNombreUsuario(),
                usuario.getRol().getIdRol(),
                usuario.getArea().getIdArea(),
                usuario.getIdUsuario()
        );
    }
}