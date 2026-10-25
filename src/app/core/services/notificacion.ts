import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { Notificacion } from '../models/notificacion.model';  

@Injectable({
    providedIn: 'root'
})
export class NotificacionService {
    private apiUrl = `${environment.apiUrl}/notificaciones`;

    constructor(private http: HttpClient){}

    listarPorUsuario(idUsuarioDestino: number): Observable<Notificacion[]> {
        return this.http.get<Notificacion[]>(`${this.apiUrl}/usuario/${idUsuarioDestino}`);
    }

    listarPorCiudadano(idReclamo: number): Observable<Notificacion[]>{
        return this.http.get<Notificacion[]>(`${this.apiUrl}/reclamo/${idReclamo}`)
    }
}
