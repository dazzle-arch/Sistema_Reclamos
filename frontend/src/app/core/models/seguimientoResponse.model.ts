export interface SeguimientoResponse {
    idSeguimiento: number;
    fechaHora: string;
    estado: string;
    observacion: string | null;
    respuesta: string | null;
    accionRealizada: string;
    idReclamo: number;
    idUsuario: number;
    nombreUsuario: string;
}