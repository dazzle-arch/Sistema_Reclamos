import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

// Importación de tus componentes compartidos
import { SidebarFuncionario } from '../../../shared/components/sidebar-funcionario/sidebar-funcionario';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';

// Importación de servicios y modelos
import { ReclamoService } from '../../../core/services/reclamo'; // O reclamo.service.ts según como lo dejaste
import { AuthService } from '../../../core/services/auth.service';
import { ReclamoResponse } from '../../../core/models/reclamoResponse.model';

@Component({
  selector: 'app-bandeja-casos',
  standalone: true,
  imports: [CommonModule, RouterLink, SidebarFuncionario, CampanaNotificaciones],
  templateUrl: './bandeja-casos.html',
  styleUrl: './bandeja-casos.css' // Si no tienes CSS, puedes borrar esta línea
})
export class BandejaCasos implements OnInit {
  
  // Aquí se guardarán los datos que lleguen de PostgreSQL
  reclamosAsignados: ReclamoResponse[] = [];
  cargando = true;

  constructor(
    private reclamoService: ReclamoService,
    private cdr: ChangeDetectorRef,
    private authService: AuthService
  ) {}

  // Este método se ejecuta automáticamente al abrir la página
  ngOnInit(): void {
    this.cargarReclamos();
  }

  cargarReclamos(): void {
    this.reclamoService.listarMisCasos().subscribe({
      next: (data) => {
        const valoresPrioridad: { [key: string]: number } = {
          'MUY ALTA': 1, 'ALTA': 2, 'MEDIA': 3, 'BAJA': 4
        };

        // Envolvemos 'data' en [...data] para crear una nueva referencia en memoria
        // Esto obliga al @for de Angular a darse cuenta de que hay datos nuevos
        this.reclamosAsignados = [...data].sort((a, b) => {
          const valorA = valoresPrioridad[a.prioridad?.toUpperCase()] || 99;
          const valorB = valoresPrioridad[b.prioridad?.toUpperCase()] || 99;
          return valorA - valorB;
        });

        this.cargando = false;
        
        // Disparamos el repintado manual de la tabla en el HTML
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Error al cargar la bandeja desde el servidor', err);
        this.cargando = false;
      }
    });
  }
}