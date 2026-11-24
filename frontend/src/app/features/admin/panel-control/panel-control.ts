import { AfterViewInit, Component, OnDestroy, signal } from '@angular/core';
import { Chart } from 'chart.js/auto';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
import { PanelService } from '../../../core/services/panel';
import { ResumenPanel } from '../../../core/models/resumenPanel.model';

@Component({
  imports: [SidebarAdmin, CampanaNotificaciones],
  selector: 'app-panel-control',
  styleUrl: './panel-control.css',
  templateUrl: './panel-control.html',
})
export class PanelControl implements AfterViewInit, OnDestroy {
  resumen = signal<ResumenPanel | null>(null);
  cargando = signal(true);
  error = signal('');
  sinCerrados = signal(false);

  private graficos: Chart[] = [];

  constructor(private panelService: PanelService) {}

  ngAfterViewInit(): void {
    this.panelService.resumen().subscribe({
      next: (r) => {
        this.resumen.set(r);
        this.cargando.set(false);
        this.dibujar(r);
      },
      error: (err) => {
        this.cargando.set(false);
        this.error.set(err.error?.mensaje ?? 'No se pudo cargar el panel.');
      }
    });
  }

  ngOnDestroy(): void {
    this.graficos.forEach(g => g.destroy());
  }

  private dibujar(r: ResumenPanel): void {
    this.graficos.forEach(g => g.destroy());
    this.graficos = [];

    const enteros = { beginAtZero: true, ticks: { precision: 0 } };

    this.graficos.push(new Chart('lineChart', {
      type: 'line',
      data: {
        labels: r.porMes.map(m => m.etiqueta),
        datasets: [{ data: r.porMes.map(m => m.cantidad), borderColor: '#4285f4', tension: 0.3 }]
      },
      options: { plugins: { legend: { display: false } }, scales: { y: enteros } }
    }));

    this.graficos.push(new Chart('pieChart', {
      type: 'pie',
      data: {
        labels: r.porCategoria.map(c => c.etiqueta),
        datasets: [{
          data: r.porCategoria.map(c => c.cantidad),
          backgroundColor: ['#4285f4', '#a78bfa', '#fbbf24', '#facc15', '#34d399', '#fb7185', '#38bdf8']
        }]
      }
    }));

    this.graficos.push(new Chart('barChart', {
      type: 'bar',
      data: {
        labels: ['Registrado', 'En proceso', 'Resuelto', 'Cerrado'],
        datasets: [{
          label: 'Cantidad',
          data: [
            r.porEstado['REGISTRADO'] ?? 0,
            r.porEstado['EN_PROCESO'] ?? 0,
            r.porEstado['RESUELTO'] ?? 0,
            r.porEstado['CERRADO'] ?? 0
          ],
          backgroundColor: ['#dc3545', '#fd7e14', '#ffc107', '#198754']
        }]
      },
      options: { indexAxis: 'y', plugins: { legend: { display: false } }, scales: { x: enteros } }
    }));

    const aTiempo = r.plazos.cerradosATiempo;
    const tarde = r.plazos.cerradosTarde;
    this.sinCerrados.set(aTiempo + tarde === 0);
    this.graficos.push(new Chart('doughnutChart', {
      type: 'doughnut',
      data: {
        labels: ['A tiempo', 'Fuera de plazo'],
        datasets: [{ data: [aTiempo, tarde], backgroundColor: ['#198754', '#dc3545'] }]
      }
    }));
  }
}