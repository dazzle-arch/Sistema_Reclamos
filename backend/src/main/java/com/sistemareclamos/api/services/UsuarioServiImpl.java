package com.sistemareclamos.api.services;

import com.sistemareclamos.api.dtos.UsuarioRequestDTO;
import com.sistemareclamos.api.dtos.UsuarioResponseDTO;
import com.sistemareclamos.api.entities.Area;
import com.sistemareclamos.api.entities.Rol;
import com.sistemareclamos.api.entities.Usuario;
import com.sistemareclamos.api.repositories.AreaRepository;
import com.sistemareclamos.api.repositories.RolRepository;
import com.sistemareclamos.api.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiImpl implements UsuarioService {

    private final UsuarioRepository repository;
    private final RolRepository rolRepository;
    private final AreaRepository areaRepository;

    @Override
    public List<UsuarioResponseDTO> listar() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public UsuarioResponseDTO obtenerPorId(Long id) {
        return repository.findById(id).map(this::toResponse).orElse(null);
    }

    @Override
    public UsuarioResponseDTO obtenerPorNombreUsuario(String nombreUsuario) {
        return repository.findByNombreUsuario(nombreUsuario).map(this::toResponse).orElse(null);
    }

    @Override
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        if (dto.contrasena() == null || dto.contrasena().isBlank()) {
            return null; // contraseña obligatoria al crear
        }
        if (repository.existsByCorreo(dto.correo())
                || repository.existsByNombreUsuario(dto.nombreUsuario())) {
            return null; // correo o nombre de usuario repetido
        }
        Rol rol = rolRepository.findById(dto.idRol()).orElse(null);
        Area area = areaRepository.findById(dto.idArea()).orElse(null);
        if (rol == null || area == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNombreUsuario(dto.nombreUsuario());
        usuario.setNombres(dto.nombres());
        usuario.setCorreo(dto.correo());
        usuario.setContrasena(dto.contrasena());
        usuario.setRol(rol);
        usuario.setArea(area);

        return toResponse(repository.save(usuario));
    }

    @Override
    public UsuarioResponseDTO actualizar(Long id, UsuarioRequestDTO dto) {
        Usuario actual = repository.findById(id).orElse(null);
        if (actual == null) {
            return null; // no existe
        }
        if (repository.existsByCorreoAndIdUsuarioNot(dto.correo(), id)
                || repository.existsByNombreUsuarioAndIdUsuarioNot(dto.nombreUsuario(), id)) {
            return null; // repetido en otro usuario
        }
        Rol rol = rolRepository.findById(dto.idRol()).orElse(null);
        Area area = areaRepository.findById(dto.idArea()).orElse(null);
        if (rol == null || area == null) {
            return null;
        }

        actual.setNombreUsuario(dto.nombreUsuario());
        actual.setNombres(dto.nombres());
        actual.setCorreo(dto.correo());
        if (dto.contrasena() != null && !dto.contrasena().isBlank()) {
            actual.setContrasena(dto.contrasena());
        }
        actual.setRol(rol);
        actual.setArea(area);

        return toResponse(repository.save(actual));
    }

    @Override
    public boolean eliminar(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
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
