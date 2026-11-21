import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';

import { AreaService } from '../../../core/services/area';
import { AreaResponse } from '../../../core/models/areaResponse.model';
import { SidebarFuncionario } from '../../../shared/components/sidebar-funcionario/sidebar-funcionario';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
import { ReclamoService } from '../../../core/services/reclamo';
import { ReclamoResponse } from '../../../core/models/reclamoResponse.model';
import { AtencionRequest } from '../../../core/models/atencionRequest.model';

@Component({
  selector: 'app-atender-caso',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, SidebarFuncionario, CampanaNotificaciones],
  templateUrl: './atender-caso.html'
})
export class AtenderCaso implements OnInit {
  
  idCaso!: number;
  reclamo!: ReclamoResponse;
  cargando = true;
  atencionForm!: FormGroup;
  areas: AreaResponse[] = [];
  mensajeExito = '';
  guardando = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reclamoService: ReclamoService,
    private areaService: AreaService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef
  ) {
    this.inicializarFormulario();
  }

  // MÉTODO FUSIONADO: Todo el código de inicialización va aquí adentro
  ngOnInit(): void {
    // 1. Extraemos el ID de la URL dinámica
    this.idCaso = Number(this.route.snapshot.paramMap.get('id'));
    
    // 2. Pedimos los datos del caso al backend
    this.cargarDatosReclamo();

    // 3. Llamada para cargar las áreas simuladas
    this.cargarAreas();

    // 4. Escuchamos los cambios del campo "nuevoEstado" en tiempo real
    this.atencionForm.get('nuevoEstado')?.valueChanges.subscribe(estadoSeleccionado => {
      const derivarControl = this.atencionForm.get('idAreaDerivada');
      const prioridadControl = this.atencionForm.get('nuevaPrioridad');

      if (estadoSeleccionado === 'CERRADO') {
        derivarControl?.disable();
        derivarControl?.setValue('');

        prioridadControl?.disable();
        prioridadControl?.setValue('');
      } else {
        derivarControl?.enable();
        prioridadControl?.enable();
      }
    });
  }

  inicializarFormulario() {
    this.atencionForm = this.fb.group({
      nuevoEstado: ['', Validators.required],
      nuevaPrioridad: [''],
      accionRealizada: ['', Validators.required],
      observacion: [''],
      respuestaCiudadano: [''],
      idAreaDerivada: [''] // Por ahora lo dejamos simple
    });
  }

  cargarDatosReclamo() {
    this.reclamoService.buscarPorId(this.idCaso).subscribe({
      next: (data) => {
        console.log('Datos exactos del caso recibidos:', data);
        this.reclamo = data;
        this.cargando = false;
        this.cdr.detectChanges(); 
      },
      error: (err) => {
        console.error('Error al cargar el caso', err);
        this.cargando = false;
        this.cdr.detectChanges(); 
      }
    });
  }

  guardarAtencion() {
    if (this.atencionForm.invalid || this.guardando) {
      return;
    }

    // getRawValue() lee todos los campos aunque idAreaDerivada esté deshabilitado
    this.guardando = true;
    const raw = this.atencionForm.getRawValue();
    const payload: AtencionRequest = {
      ...raw,
      idAreaDerivada: raw.idAreaDerivada ? Number(raw.idAreaDerivada) : null,
      nuevaPrioridad: raw.nuevaPrioridad || null
    };

    this.reclamoService.registrarAtencion(this.idCaso, payload).subscribe({
      next: () => {
        this.mensajeExito = '¡Atención registrada correctamente! Redirigiendo...';
        this.cdr.detectChanges();

        setTimeout(() => {
          this.router.navigate(['/funcionario/bandeja-casos']);
        }, 2000);
      },
      error: (err) => {
        console.error('Error al guardar atención:', err);
        this.guardando = false;
        this.cdr.detectChanges();
      }
    });
  }

  cargarAreas() {
    this.areaService.listarAreas().subscribe({
      next: (data) => {
        this.areas = data; // Ahora se llena con las áreas que vienen de PostgreSQL
        this.cdr.detectChanges(); // Repintamos el HTML
      },
      error: (err) => console.error('Error al cargar las áreas', err)
    });
  }

  // Decide si la vista es solo lectura según el estado del reclamo
  get esSoloLectura(): boolean {
    if (!this.reclamo) return true;
    // Si quieres que RESUELTO todavía pueda pasarse a CERRADO, deja solo: === 'CERRADO'
    return this.reclamo.estado === 'CERRADO' || this.reclamo.estado === 'RESUELTO';
  }
}