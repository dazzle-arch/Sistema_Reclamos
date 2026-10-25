import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { Categoria } from '../models/categoria.model';

@Injectable({
    providedIn: 'root'
})
export class CategoriaService {
    private apiUrl = `${environment.apiUrl}/categorias`;

    constructor(private http: HttpClient) {}

    listar(): Observable<Categoria[]> {
        return of([
        { idCategoria: 1, nombre: 'Infraestructura' },
        { idCategoria: 2, nombre: 'Limpieza' },
        { idCategoria: 3, nombre: 'Seguridad' },
        { idCategoria: 4, nombre: 'Transporte' },
        { idCategoria: 5, nombre: 'Otro' },
        ]);
    // Cuando el backend esté listo:
    // return this.http.get<Categoria[]>(this.apiUrl);
    }

    crear(categoria: Categoria): Observable<Categoria> {
        return this.http.post<Categoria>(this.apiUrl, categoria);
    }
}