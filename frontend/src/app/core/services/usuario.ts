import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { UsuarioRequest } from '../models/usuarioRequest.model';
import { UsuarioResponse } from '../models/usuarioResponse.model';

@Injectable({providedIn: 'root'})
export class UsuarioService {
    private apiUrl = `${environment.apiUrl}/usuarios`;

    constructor(private http: HttpClient) {}

    listar(): Observable<UsuarioResponse[]> {
        return this.http.get<UsuarioResponse[]>(this.apiUrl);
    }

    listarActivosPorArea(idArea: number): Observable<UsuarioResponse[]> {
        return this.http.get<UsuarioResponse[]>(`${this.apiUrl}/area/${idArea}`)
    }
    
    buscarPorId(id: number): Observable<UsuarioResponse> {
        return this.http.get<UsuarioResponse>(`${this.apiUrl}/${id}`);
    }

    crear(usuario: UsuarioRequest): Observable<UsuarioResponse> {
        return this.http.post<UsuarioResponse>(this.apiUrl, usuario);
    }

    actualizar(id: number, usuario: UsuarioRequest): Observable<UsuarioResponse> {
        return this.http.put<UsuarioResponse>(`${this.apiUrl}/${id}`, usuario);
    }

    cambiarEstado(id: number, estado: boolean): Observable<UsuarioResponse> {
        return this.http.patch<UsuarioResponse>(`${this.apiUrl}/${id}/estado`, null, { params: { estado } });
    }
}
