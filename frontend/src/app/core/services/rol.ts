import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { RolResponse } from '../models/rolResponse.model';
import { RolRequest } from '../models/rolRequest.model';

@Injectable({
    providedIn: 'root'
})
export class RolService {
    private apiUrl = `${environment.apiUrl}/roles`;

    constructor(private http: HttpClient) {}

    listar(): Observable<RolResponse[]> {
        return this.http.get<RolResponse[]>(this.apiUrl);
    }

    crear(rol: RolRequest): Observable<RolResponse> {
        return this.http.post<RolResponse>(this.apiUrl, rol);
    }

    actualizar(id: number, rol: RolRequest): Observable<RolResponse> {
        return this.http.put<RolResponse>(`${this.apiUrl}/${id}`, rol);
    }

    eliminar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}