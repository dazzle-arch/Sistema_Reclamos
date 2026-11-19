import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';
import { map } from 'rxjs/operators';
import { ReclamoRequest } from '../models/reclamoRequest.model';
import { ReclamoResponse } from '../models/reclamoResponse.model';
import { environment } from '../../../environments/environment.development';
import { DerivarRequest } from '../models/derivarRequest.model';

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

    derivar(id: number, body: DerivarRequest): Observable<ReclamoResponse> {
        return this.http.patch<ReclamoResponse>(`${this.apiUrl}/${id}/derivar`, body);
    }

    listarMisCasos(): Observable<ReclamoResponse[]> {
        return this.http.get<any>(`${this.apiUrl}/mis-casos`).pipe(
            map(response => {
                // Si la respuesta es un array puro, devuélvela tal cual
                if (Array.isArray(response)) {
                    return response;
                }
                // Si viene paginada por Spring Data, extrae el array de 'content'
                if (response && response.content && Array.isArray(response.content)) {
                    return response.content;
                }
                // Fallback por defecto
                return [];
            })
        );
    }

    // 2. Método para enviar el formulario de atención a la nueva tabla que creamos
    registrarAtencion(id: number, body: any): Observable<ReclamoResponse> {
        return this.http.post<ReclamoResponse>(`${this.apiUrl}/${id}/atenciones`, body);
    }
}

