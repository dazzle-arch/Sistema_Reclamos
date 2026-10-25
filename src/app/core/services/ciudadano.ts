import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { Ciudadano } from '../models/ciudadano.model';

@Injectable({
    providedIn: 'root'
})
export class CiudadanoService {
    private apiUrl = `${environment.apiUrl}/ciudadanos`;

    constructor(private http: HttpClient){}

    listar(): Observable<Ciudadano[]> {
        return this.http.get<Ciudadano[]>(this.apiUrl);
    }

    buscarPorId(id: number): Observable<Ciudadano> {
        return this.http.get<Ciudadano>(`${this.apiUrl}/${id}`)
    }

    crear(ciudadano: Ciudadano): Observable<Ciudadano>{
        return this.http.put<Ciudadano>(this.apiUrl, ciudadano)
    }
    actualizar(id: number, ciudadano: Ciudadano): Observable<Ciudadano> {
        return this.http.put<Ciudadano>(`${this.apiUrl}/${id}`, ciudadano);
    }
    eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
