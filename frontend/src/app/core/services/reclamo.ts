import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ReclamoRequest } from '../models/reclamoRequest.model';
import { ReclamoResponse } from '../models/reclamoResponse.model';
import { ReclamoCreado } from '../models/reclamoCreado.model';
import { ConsultaPublica } from '../models/consultaPublica.model';
import { DerivarRequest } from '../models/derivarRequest.model';
import { AtencionRequest } from '../models/atencionRequest.model';
import { environment } from '../../../environments/environment.development';

@Injectable({
    providedIn: 'root'
})
export class ReclamoService {
    private apiUrl = `${environment.apiUrl}/reclamos`;

    constructor(private http: HttpClient) {}

    listar(): Observable<ReclamoResponse[]> {
        return this.http.get<ReclamoResponse[]>(this.apiUrl);
    }

    buscarPorId(id: number): Observable<ReclamoResponse> {
        return this.http.get<ReclamoResponse>(`${this.apiUrl}/${id}`);
    }

    crear(reclamo: ReclamoRequest): Observable<ReclamoCreado> {
        return this.http.post<ReclamoCreado>(this.apiUrl, reclamo);
    }

    consultar(codigo: number, dni: string): Observable<ConsultaPublica> {
        return this.http.get<ConsultaPublica>(`${this.apiUrl}/consulta`, { params: { codigo, dni } });
    }

    actualizarEstado(id: number, estado: string): Observable<ReclamoResponse> {
        return this.http.patch<ReclamoResponse>(`${this.apiUrl}/${id}/estado`, null, { params: { estado } });
    }

    derivar(id: number, body: DerivarRequest): Observable<ReclamoResponse> {
        return this.http.patch<ReclamoResponse>(`${this.apiUrl}/${id}/derivar`, body);
    }

    listarMisCasos(): Observable<ReclamoResponse[]> {
        return this.http.get<ReclamoResponse[]>(`${this.apiUrl}/mis-casos`);
    }

    registrarAtencion(id: number, body: AtencionRequest): Observable<ReclamoResponse> {
        return this.http.post<ReclamoResponse>(`${this.apiUrl}/${id}/atenciones`, body);
    }
}

