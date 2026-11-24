import { Component, OnInit, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CategoriaService } from '../../../core/services/categoria';
import { ReclamoService } from '../../../core/services/reclamo';
import { CategoriaResponse } from '../../../core/models/categoriaResponse.model';
import { ReclamoRequest } from '../../../core/models/reclamoRequest.model';
import { DocumentoService } from '../../../core/services/documento';

@Component({
  imports: [ReactiveFormsModule, RouterLink],
  selector: 'app-registrar',
  styleUrl: './registrar.css',
  templateUrl: './registrar.html',
})
export class Registrar implements OnInit{
  formularioReclamo: FormGroup;
  categorias = signal<CategoriaResponse[]>([]);
  enviando = signal(false);
  codigoRegistrado = signal<number | null>(null);
  mensajeError = signal('');
  copiado = signal(false);
  archivo: File | null = null;
  errorArchivo = signal('');
  avisoEvidencia = signal('');

  constructor(
    private fb: FormBuilder, 
    private categoriaService : CategoriaService,
    private reclamoService: ReclamoService,
    private router: Router,
    private documentoService: DocumentoService
  ){

    this.formularioReclamo = this.fb.group({
      apellidos: ['', Validators.required],
      nombres: ['', Validators.required],
      // exactamente 8 dígitos
      dni: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      // solo números, exactamente 9 dígitos
      telefono: ['', [Validators.required, Validators.pattern('^[0-9]{9}$')]],
      sexo: ['', Validators.required],
      // debe tener formato de email (@)
      correo: ['', [Validators.required, Validators.email]],
      tipoReclamo: ['', Validators.required],
      descripcion: ['', [Validators.required, Validators.minLength(10)]],
      asunto: ['', [Validators.required, Validators.maxLength(150)]]
    })
  }

  ngOnInit(): void {
    this.categoriaService.listar().subscribe({
      next: (data) => this.categorias.set(data),
      error: (error) => console.error('Error al cargar categorias', error)
    })
  }

  alElegirArchivo(event: Event): void {
  const input = event.target as HTMLInputElement;
  const f = input.files?.[0] ?? null;
  this.errorArchivo.set('');
  this.archivo = null;
  if (!f) return;

  if (!['application/pdf', 'image/jpeg', 'image/png'].includes(f.type)) {
    this.errorArchivo.set('Solo se permiten archivos PDF, JPG o PNG');
    input.value = '';
    return;
  }
  if (f.size > 5 * 1024 * 1024) {
    this.errorArchivo.set('El archivo supera el máximo de 5 MB');
    input.value = '';
    return;
  }
  this.archivo = f;
}

  enviarDatos() {
    if (this.formularioReclamo.invalid || this.enviando()) {
      this.formularioReclamo.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.mensajeError.set('');
    this.avisoEvidencia.set('');

    const dni: string = this.formularioReclamo.value.dni;   // se guarda antes del reset
    const archivo = this.archivo;
    const payLoad: ReclamoRequest = {
      ...this.formularioReclamo.value,
      tipoReclamo: Number(this.formularioReclamo.value.tipoReclamo)
    };

    this.reclamoService.crear(payLoad).subscribe({
      next: (r) => {
        this.formularioReclamo.reset();
        this.archivo = null;

        if (!archivo) {
          this.enviando.set(false);
          this.codigoRegistrado.set(r.idReclamo);
          return;
        }
        this.documentoService.subirEvidencia(r.idReclamo, dni, archivo).subscribe({
          next: () => {
            this.enviando.set(false);
            this.codigoRegistrado.set(r.idReclamo);
          },
          error: () => {
            // El reclamo ya existe: se muestra el código igual
            this.avisoEvidencia.set('Su reclamo fue registrado, pero la evidencia no pudo adjuntarse.');
            this.enviando.set(false);
            this.codigoRegistrado.set(r.idReclamo);
          }
        });
      },
      error: (err) => {
        this.enviando.set(false);
        this.mensajeError.set(err.error?.mensaje ?? 'No se pudo registrar el reclamo. Intentalo nuevamente.');
      }
    });
}

  copiarCodigo(): void {
    const codigo = this.codigoRegistrado();
    if (codigo === null) return;

    navigator.clipboard.writeText(String(codigo)).then(() => {
      this.copiado.set(true);
      setTimeout(() => this.copiado.set(false), 2000);
    });
  }

  confirmarYVolver(): void {
    this.codigoRegistrado.set(null);
    this.router.navigate(['/home']);
  }
}
