import { Component, OnInit, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CategoriaService } from '../../../core/services/categoria';
import { ReclamoService } from '../../../core/services/reclamo';
import { CategoriaResponse } from '../../../core/models/categoriaResponse.model';
import { ReclamoRequest } from '../../../core/models/reclamoRequest.model';

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

  constructor(
    private fb: FormBuilder, 
    private categoriaService : CategoriaService,
    private reclamoService: ReclamoService,
    private router: Router
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
      tipoReclamo: ['', Validators.required, Validators.maxLength(150)],
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

  enviarDatos(){
    if(this.formularioReclamo.invalid || this.enviando()){
      this.formularioReclamo.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.mensajeError.set('');

    const payLoad: ReclamoRequest = {
      ...this.formularioReclamo.value,
      tipoReclamo: Number(this.formularioReclamo.value.tipoReclamo)
    };

    this.reclamoService.crear(payLoad).subscribe({
      next: (r) => {
        this.enviando.set(false);
        this.formularioReclamo.reset();
        this.codigoRegistrado.set(r.idReclamo);
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
