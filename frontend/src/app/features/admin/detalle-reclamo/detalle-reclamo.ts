import { Component, OnInit, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin'; 
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
import { ReclamoService } from '../../../core/services/reclamo';
import { CiudadanoService } from '../../../core/services/ciudadano';
import { SeguimientoService } from '../../../core/services/seguimiento';
import { ReclamoResponse } from '../../../core/models/reclamoResponse.model';
import { Ciudadano } from '../../../core/models/ciudadano.model';
import { SeguimientoResponse } from '../../../core/models/seguimientoResponse.model';

@Component({
  imports: [SidebarAdmin, CampanaNotificaciones, RouterLink, DatePipe],
  selector: 'app-detalle-reclamo',
  styleUrl: './detalle-reclamo.css',
  templateUrl: './detalle-reclamo.html',
})
export class DetalleReclamo implements OnInit {
  reclamo = signal<ReclamoResponse | null>(null);
  ciudadano = signal<Ciudadano | null>(null);
  historial = signal<SeguimientoResponse[]>([]);
  error = signal(false);

  constructor(
    private route: ActivatedRoute,
    private reclamoService: ReclamoService,
    private ciudadanoService: CiudadanoService,
    private seguimientoService: SeguimientoService
  ) {}

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.reclamoService.buscarPorId(id).subscribe({
      next: (r) => {
        this.reclamo.set(r);
        this.ciudadanoService.buscarPorId(r.idCiudadano).subscribe({
          next: (c) => this.ciudadano.set(c)
        });
      },
      error: () => this.error.set(true)
    });

    this.seguimientoService.listarPorReclamo(id).subscribe({
      next: (h) => this.historial.set(h)
    });
  }

  claseEstado(estado: string): string{
    switch(estado){
      case 'EN_PROCESO': return 'bg-warning text-dark';
      case 'RESUELTO': return 'bg-info text-dark';
      case 'CERRADO': return 'bg-success';
      default: return 'bg-secondary';
    }
  }

}