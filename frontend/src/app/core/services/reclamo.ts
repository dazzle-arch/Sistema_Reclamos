import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';
import { ReclamoResponse } from '../models/reclamoResponse.mode';
import { ReclamoRequest } from '../models/reclamoRequest.model';
import { environment } from '../../../environments/environment.development';

@Injectable({
    providedIn: 'root'
})
export class ReclamoService{
    private apiUrl = `${environment.apiUrl}/reclamos`

    constructor(private http: HttpClient){}

    listar(): Observable<ReclamoResponse[]>{
        return this.http.get<ReclamoResponse[]>(this.apiUrl)
    }

    buscarPorId(id: number): Observable<ReclamoResponse> {
        return this.http.get<ReclamoResponse>(`${this.apiUrl}/${id}`)
    }

    crear(reclamo: ReclamoRequest): Observable<ReclamoResponse> {
        return this.http.post<ReclamoResponse>(this.apiUrl, reclamo);
    }

    consultar(codigo: number, dni: string): Observable<ReclamoResponse> {
        return this.http.get<ReclamoResponse>(`${this.apiUrl}/consulta`, { params: { codigo, dni } });
    }

    actualizarEstado(id: number, estado: string): Observable<ReclamoResponse> {
        return this.http.patch<ReclamoResponse>(`${this.apiUrl}/${id}/estado`, null, { params: { estado } });
}
}

