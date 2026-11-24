export interface NotificacionResponse {
    idNotificacion: number;
    idReclamo: number;
    idUsuarioDestino: number | null;
    asunto: string;
    mensaje: string;
    fechaEnvio: string;
    estado: string;
    leida: boolean;
}