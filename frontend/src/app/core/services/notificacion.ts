import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { NotificacionResponse } from '../models/notificacionResponse.model';

@Injectable({
    providedIn: 'root'
})
export class NotificacionService {
    private apiUrl = `${environment.apiUrl}/notificaciones`;

    constructor(private http: HttpClient) {}

    listarMisNotificaciones(): Observable<NotificacionResponse[]> {
        return this.http.get<NotificacionResponse[]>(`${this.apiUrl}/mis-notificaciones`);
    }

    contarNoLeidas(): Observable<{ cantidad: number }> {
        return this.http.get<{ cantidad: number }>(`${this.apiUrl}/mis-notificaciones/no-leidas/cantidad`);
    }

    marcarLeida(id: number): Observable<NotificacionResponse> {
        return this.http.patch<NotificacionResponse>(`${this.apiUrl}/${id}/leida`, null);
    }

    marcarTodasLeidas(): Observable<{ actualizadas: number }> {
        return this.http.patch<{ actualizadas: number }>(`${this.apiUrl}/leidas`, null);
    }
}
