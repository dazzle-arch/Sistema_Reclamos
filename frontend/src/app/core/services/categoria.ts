import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { CategoriaResponse } from '../models/categoriaResponse.model';
import { CategoriaRequest } from '../models/categoriaRequest.model';

@Injectable({
    providedIn: 'root'
})
export class CategoriaService {
    private apiUrl = `${environment.apiUrl}/categorias`;

    constructor(private http: HttpClient) {}

    listar(): Observable<CategoriaResponse[]> {
        return this.http.get<CategoriaResponse[]>(this.apiUrl);
    }

    crear(categoria: CategoriaRequest): Observable<CategoriaRequest> {
        return this.http.post<CategoriaRequest>(this.apiUrl, categoria);
    }

    actualizar(id: number, categoria: CategoriaRequest): Observable<CategoriaResponse> {
        return this.http.put<CategoriaResponse>(`${this.apiUrl}/${id}`, categoria);
        }
    
    eliminar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}