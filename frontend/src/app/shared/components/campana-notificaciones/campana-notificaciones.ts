import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Subscription, catchError, of, switchMap, timer } from 'rxjs';
import { NotificacionService } from '../../../core/services/notificacion';
import { NotificacionEstadoService } from '../../../core/services/notificacion-estado';

@Component({
  imports: [RouterLink],
  selector: 'app-campana-notificaciones',
  styleUrl: './campana-notificaciones.css',
  templateUrl: './campana-notificaciones.html',
})
export class CampanaNotificaciones implements OnInit {
  private sub?: Subscription;

  constructor(
    private notificacionService: NotificacionService,
    public estado: NotificacionEstadoService
  ) {}

  ngOnInit(): void {
    this.sub = timer(0, 30000).pipe(
      switchMap(() => this.notificacionService.contarNoLeidas()
        .pipe(catchError(() => of({ cantidad: 0 }))))
    ).subscribe(r => this.estado.noLeidas.set(r.cantidad));
  }

  ngOnDestroy(): void {
    this.sub?.unsubscribe();
  }
}