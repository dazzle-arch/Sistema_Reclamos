export interface Reclamo {
    idReclamo?: number,
    idCiudadano: number,
    idCategoria: number,
    asunto: string;
    descripcion: string,
    estado: string,
    fechaRegistro: string,
    prioridad: string
}