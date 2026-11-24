package com.sistemareclamos.api.services;
import com.sistemareclamos.api.dtos.*;
import com.sistemareclamos.api.security.UsuarioPrincipal;

import java.util.List;

public interface ReclamoService {
    List<ReclamoResponseDTO> listar();
    ReclamoResponseDTO obtenerPorId(Long id, UsuarioPrincipal usuario);
    ReclamoCreadoDTO crear(ReclamoRequestDTO dto);
    ReclamoResponseDTO actualizarEstado(Long id, String nuevoEstado, UsuarioPrincipal usuario);
    ReclamoResponseDTO derivar(Long id, DerivarRequestDTO dto, UsuarioPrincipal usuario);
    ReclamoResponseDTO registrarAtencion(Long id, AtencionRequestDTO dto, UsuarioPrincipal usuario);
    List<ReclamoResponseDTO> listarMisCasos(UsuarioPrincipal usuario);
    ConsultaPublicaDTO consultar(Long codigo, String dni);
}
