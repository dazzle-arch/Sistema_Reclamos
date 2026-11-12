import { Component, OnInit, signal, computed} from '@angular/core';
import { DatePipe } from '@angular/common';
import { RouterLink } from '@angular/router';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';  
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
import { ReclamoService } from '../../../core/services/reclamo';
import { ReclamoResponse } from '../../../core/models/reclamoResponse.model';

@Component({
  imports: [SidebarAdmin, CampanaNotificaciones, RouterLink, DatePipe],
  selector: 'app-bandeja-casos',
  styleUrl: './bandeja-casos.css',
  templateUrl: './bandeja-casos.html',
})
export class BandejaCasos implements OnInit {
  reclamos = signal<ReclamoResponse[]>([]);
  cargando = signal(true);
  error = signal(false);

  constructor(private reclamoService: ReclamoService) {}

  ngOnInit(): void {
    this.reclamoService.listar().subscribe({
      next: (data) => {
        this.reclamos.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set(true);
        this.cargando.set(false);
      }
    })
  }

  porcentajeCerrados = computed(() => {
    const lista = this.reclamos();
    if(lista.length === 0) return 0;
    const cerrados = lista.filter(r => r.estado === 'CERRADO').length;
    return Math.round((cerrados /lista.length) * 100);
  });

  claseEstado(estado: string): string {
    switch(estado) {
      case 'EN_PROCESO': return 'bg-warning text-dark';
      case 'RESUELTO': return 'bg-info text-dark';
      case 'CERRADO': return 'bg-success';
      default: return 'bg-secondary';
    }
  }
}
