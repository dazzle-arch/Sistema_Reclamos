import { AfterViewInit, Component } from '@angular/core';
import { Chart } from 'chart.js/auto';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';  

@Component({
  imports: [SidebarAdmin],
  selector: 'app-panel-control',
  styleUrl: './panel-control.css',
  templateUrl: './panel-control.html',
})
export class PanelControl implements AfterViewInit {
  ngAfterViewInit(): void {
    new Chart('lineChart',{
      type: 'line',
      data: {
        labels: ['Ene', 'Feb', 'Mar', 'Abr', 'May'],
        datasets: [{
          data:[8, 18, 20, 33, 36],
          borderColor: '#4285f4',
          tension: 0.3,
        }]
      }
    });

    new Chart('pieChart',{
      type: 'pie',
      data: {
        labels: ['Infraestructura', 'Limpieza', 'Seguridad', 'Transporte', 'Otro'],
        datasets: [{
          data:[35, 25, 20, 15, 5],
          backgroundColor: ['#4285f4', '#a78bfa', '#fbbf24', '#facc15', '#34d399'],
        }]
      }
    });

    new Chart('barChart',{
      type: 'bar',
      data: {
        labels: ['Registrado', 'Derivado', 'Atendido', 'Cerrado'],
        datasets: [{
          label: 'Cantidad',
          data:[12, 7, 5, 3],
          backgroundColor: ['#dc3545', '#fd7e14', '#ffc107', '#198754'],
        }]
      },
      options: {indexAxis: 'y', plugins: {legend: {display: false}}}
    });

    new Chart('doughnutChart', {
      type: 'doughnut',
      data: {
        labels: ['A tiempo', 'Fuera de plazo'],
        datasets: [{
          data: [85, 15],
          backgroundColor: ['#198754', '#dc3545'],
        }]
      }
    });
  }
}
