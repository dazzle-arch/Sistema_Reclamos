import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { Rol } from '../models/rol.model';

@Injectable({
    providedIn: 'root'
})
export class RolService {
    private apiUrl = `${environment.apiUrl}/roles`;

    constructor(private http: HttpClient) {}

    listar(): Observable<Rol[]> {
        return this.http.get<Rol[]>(this.apiUrl);
    }

    crear(rol: Rol): Observable<Rol> {
        return this.http.post<Rol>(this.apiUrl, rol);
    }
}