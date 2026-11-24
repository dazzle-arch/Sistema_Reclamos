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
import { AuthService } from '../../../core/services/auth.service';
import { DocumentoService } from '../../../core/services/documento';
import { DocumentoResponse } from '../../../core/models/documentoResponse.model';

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
  errorMensaje = '';
  guardando = false;
  documentos: DocumentoResponse[] = [];

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reclamoService: ReclamoService,
    private areaService: AreaService,
    private fb: FormBuilder,
    private cdr: ChangeDetectorRef,
    private authService: AuthService,
    private documentoService: DocumentoService
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
      this.reclamo = data;
      this.documentoService.listarPorReclamo(this.idCaso).subscribe({
        next: (d) => { this.documentos = d; this.cdr.detectChanges(); }
      });
      this.cargando = false;
      if (this.esSoloLectura) this.atencionForm.disable();
      this.cdr.detectChanges();
    },
    error: (err) => {
      this.errorMensaje = err.error?.mensaje ?? 'No se pudo cargar la información del caso.';
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
    this.errorMensaje = '';
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
        this.errorMensaje = err.error?.mensaje ?? 'No se pudo guardar la atención.';
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

  descargarDocumento(d: DocumentoResponse): void {
    this.documentoService.descargar(d, () => {
      this.errorMensaje = 'No se pudo descargar el archivo.';
      this.cdr.detectChanges();
    });
  }

  /** Solo lectura: caso cerrado o asignado a otro funcionario. RESUELTO sigue editable para poder cerrarlo. */
  get esSoloLectura(): boolean {
    if (!this.reclamo) return true;
    if (this.reclamo.estado === 'CERRADO') return true;
    return this.esDeOtro;
  }

  get esDeOtro(): boolean {
    const asignado = this.reclamo?.idUsuarioAsignado;
    return asignado != null && asignado !== this.authService.obtenerIdUsuario();
  }
}