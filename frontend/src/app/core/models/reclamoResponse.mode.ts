export interface ReclamoResponse {
    idReclamo: number;
    idCiudadano: number;
    nombreCiudadano: string;
    dniCiudadano: string;
    idCategoria: number;
    nombreCategoria: string;
    asunto: string;
    descripcion: string;
    estado: string;
    prioridad: string;
    fechaRegistro: string;
}