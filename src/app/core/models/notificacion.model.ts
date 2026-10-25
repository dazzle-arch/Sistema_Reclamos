export interface Notificacion {
    idNotificacion?: number,
    idReclamo: number,
    idUsuarioDestino: number,
    asunto: string,
    mensaje:string,
    fechaEnvio: string,
    canal: string,
    estado: string
}