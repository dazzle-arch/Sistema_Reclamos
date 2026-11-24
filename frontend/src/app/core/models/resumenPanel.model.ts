export interface Conteo {
    etiqueta: string;
    cantidad: number;
}

export interface ResumenPanel {
    total: number;
    porEstado: { [estado: string]: number };
    porCategoria: Conteo[];
    porMes: Conteo[];
    semana: {
        nuevos: number;
        enProceso: number;
        resueltos: number;
        cerrados: number;
        fueraDePlazo: number;
    };
    plazos: {
        cerradosATiempo: number;
        cerradosTarde: number;
        fueraDePlazoAbiertos: number;
    };
    sinAsignar: number;
    muyAltaAbiertos: number;
}