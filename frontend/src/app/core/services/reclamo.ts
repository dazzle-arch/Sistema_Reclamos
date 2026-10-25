import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';
import { Reclamo } from '../models/reclamo.model';
import { environment } from '../../../environments/environment.development';

@Injectable({
    providedIn: 'root'
})
export class ReclamoService{
    private apiUrl = `${environment.apiUrl}/reclamos`

    constructor(private http: HttpClient){}

    listar(): Observable<Reclamo[]>{
        return this.http.get<Reclamo[]>(this.apiUrl)
    }

    buscarPorId(id: number): Observable<Reclamo> {
        return this.http.get<Reclamo>(`${this.apiUrl}/${id}`)
    }

    crear(reclamo: Reclamo): Observable<Reclamo> {
        return this.http.post<Reclamo>(this.apiUrl, reclamo)
    }

    actualizar(id: number, reclamo: Reclamo): Observable<Reclamo> {
        return this.http.put<Reclamo>(`${this.apiUrl}/${id}`, reclamo)
    }
}

