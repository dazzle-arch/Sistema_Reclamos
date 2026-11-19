export interface AtencionRequest {
    nuevoEstado: string;
    accionRealizada: string;
    observacion: string;
    respuestaCiudadano: string;
    idAreaDerivada?: number; // Opcional
}