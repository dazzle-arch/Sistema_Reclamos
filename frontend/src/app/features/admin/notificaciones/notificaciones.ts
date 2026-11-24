import { Component, OnInit, computed, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';
import { SidebarFuncionario } from '../../../shared/components/sidebar-funcionario/sidebar-funcionario';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
import { NotificacionService } from '../../../core/services/notificacion';
import { NotificacionEstadoService } from '../../../core/services/notificacion-estado';
import { AuthService } from '../../../core/services/auth.service';
import { NotificacionResponse } from '../../../core/models/notificacionResponse.model';

@Component({
  imports: [SidebarAdmin, SidebarFuncionario, CampanaNotificaciones, DatePipe],
  selector: 'app-notificaciones',
  styleUrl: './notificaciones.css',
  templateUrl: './notificaciones.html',
})
export class Notificaciones implements OnInit {
  notificaciones = signal<NotificacionResponse[]>([]);
  filtro = signal<'todas' | 'no-leidas'>('todas');
  cargando = signal(true);
  error = signal(false);
  esAdmin: boolean;

  visibles = computed(() =>
    this.filtro() === 'no-leidas'
      ? this.notificaciones().filter(n => !n.leida)
      : this.notificaciones()
  );

  constructor(
    private notificacionService: NotificacionService,
    private estado: NotificacionEstadoService,
    auth: AuthService
  ) {
    this.esAdmin = auth.obtenerIdRol() === 1;
  }

  ngOnInit(): void {
    this.notificacionService.listarMisNotificaciones().subscribe({
      next: (n) => {
        this.notificaciones.set(n);
        this.actualizarContador();
        this.cargando.set(false);
      },
      error: () => { this.error.set(true); this.cargando.set(false); }
    });
  }

  marcar(n: NotificacionResponse): void {
    if (n.leida) return;
    this.notificacionService.marcarLeida(n.idNotificacion).subscribe({
      next: () => {
        this.notificaciones.update(lista =>
          lista.map(x => x.idNotificacion === n.idNotificacion ? { ...x, leida: true } : x));
        this.actualizarContador();
      }
    });
  }

  marcarTodas(): void {
    this.notificacionService.marcarTodasLeidas().subscribe({
      next: () => {
        this.notificaciones.update(lista => lista.map(x => ({ ...x, leida: true })));
        this.actualizarContador();
      }
    });
  }

  private actualizarContador(): void {
    this.estado.noLeidas.set(this.notificaciones().filter(n => !n.leida).length);
  }
}