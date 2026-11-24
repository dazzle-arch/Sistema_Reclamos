import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class NotificacionEstadoService {
    noLeidas = signal(0);
}
