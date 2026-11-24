export interface AtencionRequest {
    nuevoEstado: string;
    accionRealizada: string;
    observacion?: string | null;
    respuestaCiudadano?: string | null;
    idAreaDerivada?: number | null;
    nuevaPrioridad?: string | null;
}