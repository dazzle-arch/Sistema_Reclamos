import { Component, OnInit, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
import { ReporteService } from '../../../core/services/reporte';
import { AreaService } from '../../../core/services/area';
import { AreaResponse } from '../../../core/models/areaResponse.model';

interface Reporte {
  tipo: string;
  nombre: string;
  descripcion: string;
  usaEstado?: boolean;
  usaArea?: boolean;
}

@Component({
  imports: [SidebarAdmin, CampanaNotificaciones, FormsModule],
  selector: 'app-reportes',
  styleUrl: './reportes.css',
  templateUrl: './reportes.html',
})
export class Reportes implements OnInit {
  readonly reportes: Reporte[] = [
    { tipo: 'estado', nombre: 'Reclamos por estado',
      descripcion: 'Cantidad y porcentaje de reclamos en cada estado.' },
    { tipo: 'area', nombre: 'Reclamos por área',
      descripcion: 'Reclamos de cada área con desglose por estado.', usaEstado: true },
    { tipo: 'categoria', nombre: 'Reclamos por categoría',
      descripcion: 'Reclamos por categoría, con su plazo y los casos vencidos.' },
    { tipo: 'funcionario', nombre: 'Carga por funcionario',
      descripcion: 'Casos abiertos, resueltos y cerrados por funcionario.', usaArea: true },
  ];

  readonly estados = [
    { valor: 'REGISTRADO', texto: 'Registrado' },
    { valor: 'EN_PROCESO', texto: 'En proceso' },
    { valor: 'RESUELTO', texto: 'Resuelto' },
    { valor: 'CERRADO', texto: 'Cerrado' },
  ];

  areas = signal<AreaResponse[]>([]);
  ocupado = signal('');
  error = signal('');

  desde = '';
  hasta = '';
  estado = '';
  idArea: number | null = null;

  constructor(private reporteService: ReporteService, private areaService: AreaService) {
    const hoy = new Date();
    const atras = new Date();
    atras.setDate(hoy.getDate() - 30);
    this.hasta = this.aTexto(hoy);
    this.desde = this.aTexto(atras);
  }

  ngOnInit(): void {
    this.areaService.listarAreas().subscribe({ next: (a) => this.areas.set(a) });
  }

  descargar(r: Reporte, formato: 'pdf' | 'xlsx'): void {
    this.error.set('');
    if (!this.desde || !this.hasta) {
      this.error.set('Indica las fechas desde y hasta.');
      return;
    }
    if (this.desde > this.hasta) {
      this.error.set('La fecha inicial no puede ser posterior a la final.');
      return;
    }

    this.ocupado.set(r.tipo + formato);
    this.reporteService.descargar(r.tipo, formato, {
      desde: this.desde,
      hasta: this.hasta,
      estado: r.usaEstado ? this.estado : '',
      idArea: r.usaArea ? this.idArea : null,
    }).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `reporte-${r.tipo}.${formato}`;
        a.click();
        URL.revokeObjectURL(url);
        this.ocupado.set('');
      },
      error: async (err) => {
        this.ocupado.set('');
        this.error.set(await this.leerMensaje(err));
      },
    });
  }

  /** Con responseType 'blob', el mensaje del backend llega dentro de un Blob. */
  private async leerMensaje(err: any): Promise<string> {
    try {
      if (err.error instanceof Blob) {
        const texto = await err.error.text();
        return JSON.parse(texto).mensaje ?? 'No se pudo generar el reporte.';
      }
    } catch { /* se usa el mensaje genérico */ }
    return 'No se pudo generar el reporte.';
  }

  private aTexto(f: Date): string {
    const m = String(f.getMonth() + 1).padStart(2, '0');
    const d = String(f.getDate()).padStart(2, '0');
    return `${f.getFullYear()}-${m}-${d}`;
  }
}