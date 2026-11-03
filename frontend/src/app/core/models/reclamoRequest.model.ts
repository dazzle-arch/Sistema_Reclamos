export interface ReclamoRequest {
    apellidos: string;
    nombres: string;
    dni: string;
    telefono: string;
    sexo: string;
    correo: string;
    tipoReclamo: number;
    asunto: string;
    descripcion: string;
    evidencia?: string;
}