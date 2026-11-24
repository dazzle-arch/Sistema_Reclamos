import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';

export interface FiltrosReporte {
    desde: string;
    hasta: string;
    estado?: string;
    idArea?: number | null;
}

@Injectable({ providedIn: 'root' })
export class ReporteService {
    private apiUrl = `${environment.apiUrl}/reportes`;

    constructor(private http: HttpClient) {}

    descargar(tipo: string, formato: 'pdf' | 'xlsx', f: FiltrosReporte): Observable<Blob> {
        let params = new HttpParams().set('formato', formato).set('desde', f.desde).set('hasta', f.hasta);
        if (f.estado) params = params.set('estado', f.estado);
        if (f.idArea) params = params.set('idArea', f.idArea);
        return this.http.get(`${this.apiUrl}/${tipo}`, { params, responseType: 'blob' });
    }
}