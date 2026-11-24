export interface MovimientoPublico {
    fecha: string;
    estado: string;
    respuesta: string | null;
}

export interface ConsultaPublica {
    codigo: number;
    asunto: string;
    categoria: string;
    area: string | null;
    estado: string;
    fechaRegistro: string;
    historial: MovimientoPublico[];
}