export interface SeguimientoResponse {
    idSeguimiento: number;
    fechaHora: string;
    estado: string;
    observacion: string;
    respuesta: string;
    accionRealizada: string;
    idReclamo: number;
    idUsuario: number;
    nombreUsuario: string;
}
export interface ReclamoResponse {
    idReclamo: number;
    idCiudadano: number;
    nombreCiudadano: string;
    dniCiudadano: string;
    idCategoria: number;
    nombreCategoria: string;
    idArea: number | null;
    nombreArea: string | null;
    idUsuarioAsignado: number | null;
    nombreUsuarioAsignado: string | null;
    asunto: string;
    descripcion: string;
    estado: string;
    prioridad: string;
    fechaRegistro: string;
    historial: SeguimientoResponse[];
}