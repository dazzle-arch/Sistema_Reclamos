export interface DocumentoResponse {
    idDocumento: number;
    nombreArchivo: string;
    tipoArchivo: string;
    descripcion: string | null;
    fechaCarga: string;
    idReclamo: number;
    origen: string | null;
    tamanio: number | null;
}