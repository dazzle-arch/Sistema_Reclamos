import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { SidebarFuncionario } from '../../../shared/components/sidebar-funcionario/sidebar-funcionario';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
import { ReclamoService } from '../../../core/services/reclamo';
import { ReclamoResponse } from '../../../core/models/reclamoResponse.model';

@Component({
  selector: 'app-casos-atendidos',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink, SidebarFuncionario, CampanaNotificaciones],
  styleUrl: './casos-atendidos.css',
  templateUrl: './casos-atendidos.html',
})
export class CasosAtendidos implements OnInit {
  casosOriginales: ReclamoResponse[] = [];
  casosFiltrados: ReclamoResponse[] = [];
  cargando = true;

  // Variables enlazadas a los filtros del HTML
  textoBusqueda = '';
  estadoSeleccionado = '';
  fechaDesde = '';
  fechaHasta = '';

  constructor(
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.cargarCasosAtendidos();
  }

  cargarCasosAtendidos(): void {
    
    this.reclamoService.listarMisCasos().subscribe({
      next: (data) => {
        // Filtramos solo los casos ya resueltos o cerrados
        this.casosOriginales = data.filter(
          (r) => r.estado === 'RESUELTO' || r.estado === 'CERRADO'
        );
        this.aplicarFiltros();
        this.cargando = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Error al cargar casos atendidos:', err);
        this.cargando = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Toma la fecha del último seguimiento registrado; si no hay, usa la fecha de registro
  obtenerFechaAtencion(caso: ReclamoResponse): string {
    if (caso.historial && caso.historial.length > 0) {
      return caso.historial[caso.historial.length - 1].fechaHora;
    }
    return caso.fechaRegistro;
  }

  aplicarFiltros(): void {
    const texto = this.textoBusqueda.trim().toLowerCase();

    this.casosFiltrados = this.casosOriginales.filter((caso) => {
      // 1. Filtro por código (ID) o nombre de ciudadano
      const coincideTexto =
        !texto ||
        caso.idReclamo.toString().includes(texto) ||
        caso.nombreCiudadano.toLowerCase().includes(texto);

      // 2. Filtro por estado (RESUELTO / CERRADO)
      const coincideEstado =
        !this.estadoSeleccionado || caso.estado === this.estadoSeleccionado;

      // 3. Filtro por rango de fechas
      const fechaCaso = new Date(this.obtenerFechaAtencion(caso));
      const coincideDesde =
        !this.fechaDesde || fechaCaso >= new Date(`${this.fechaDesde}T00:00:00`);
      const coincideHasta =
        !this.fechaHasta || fechaCaso <= new Date(`${this.fechaHasta}T23:59:59`);

      return coincideTexto && coincideEstado && coincideDesde && coincideHasta;
    });
  }
}
