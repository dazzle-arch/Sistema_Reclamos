package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.UsuarioRequestDTO;
import com.sistemareclamos.api.dtos.UsuarioResponseDTO;
import com.sistemareclamos.api.entities.Area;
import com.sistemareclamos.api.entities.Rol;
import com.sistemareclamos.api.entities.Usuario;
import com.sistemareclamos.api.exception.ConflictoException;
import com.sistemareclamos.api.exception.RecursoNoEncontradoException;
import com.sistemareclamos.api.exception.SolicitudInvalidaException;
import com.sistemareclamos.api.repositories.AreaRepository;
import com.sistemareclamos.api.repositories.RolRepository;
import com.sistemareclamos.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsuarioServiImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final RolRepository rolRepository;
    private final AreaRepository areaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UsuarioResponseDTO obtenerPorId(Long id) {
        return toResponse(buscar(id));
    }

    @Override
    public UsuarioResponseDTO obtenerPorNombreUsuario(String nombreUsuario) {
        return repository.findByNombreUsuario(nombreUsuario)
                .map(this::toResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado: " + nombreUsuario));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        if (dto.contrasena() == null || dto.contrasena().isBlank()) {
            throw new SolicitudInvalidaException("La contraseña es obligatoria al crear un usuario");
        }
        if (repository.existsByCorreo(dto.correo())) {
            throw new ConflictoException("El correo ya está registrado");
        }
        if (repository.existsByNombreUsuario(dto.nombreUsuario())) {
            throw new ConflictoException("El nombre de usuario ya está registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.nombreUsuario());
        usuario.setNombres(dto.nombres());
        usuario.setCorreo(dto.correo());
        usuario.setContrasena(passwordEncoder.encode(dto.contrasena()));
        usuario.setRol(buscarRol(dto.idRol()));
        usuario.setArea(buscarArea(dto.idArea()));

        return toResponse(repository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario actual = buscar(id);

        if (repository.existsByCorreoAndIdUsuarioNot(dto.correo(), id)) {
            throw new ConflictoException("El correo ya está registrado");
        }
        if (repository.existsByNombreUsuarioAndIdUsuarioNot(dto.nombreUsuario(), id)) {
            throw new ConflictoException("El nombre de usuario ya está registrado");
        }

        actual.setNombreUsuario(dto.nombreUsuario());
        actual.setNombres(dto.nombres());
        actual.setCorreo(dto.correo());
        if (dto.contrasena() != null && !dto.contrasena().isBlank()) {
            actual.setContrasena(passwordEncoder.encode(dto.contrasena()));
        }
        actual.setRol(buscarRol(dto.idRol()));
        actual.setArea(buscarArea(dto.idArea()));

        return toResponse(repository.save(actual));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO cambiarEstado(Long id, boolean estado) {
        Usuario usuario = buscar(id);
        usuario.setEstado(estado);
        return toResponse(repository.save(usuario));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RecursoNoEncontradoException("Usuario no encontrado con id: " + id);
        }
        repository.deleteById(id);
    }

    private Usuario buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Usuario no encontrado con id: " + id));
    }

    private Rol buscarRol(Long idRol) {
        return rolRepository.findById(idRol)
                .orElseThrow(() -> new RecursoNoEncontradoException("Rol no encontrado con id: " + idRol));
    }

    private Area buscarArea(Long idArea) {
        return areaRepository.findById(idArea)
                .orElseThrow(() -> new RecursoNoEncontradoException("Área no encontrada con id: " + idArea));
    }

    private UsuarioResponseDTO toResponse(Usuario u) {
        return new UsuarioResponseDTO(
                u.getIdUsuario(),
                u.getRol().getIdRol(),
                u.getArea().getIdArea(),
                u.getNombreUsuario(),
                u.getNombres(),
                u.getCorreo(),
                u.getEstado(),
                u.getRol().getNombre(),
                u.getArea().getNombre()
        );
    }
}
