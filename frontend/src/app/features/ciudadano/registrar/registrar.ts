import { Component, OnInit, signal } from '@angular/core';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CategoriaService } from '../../../core/services/categoria';
import { ReclamoService } from '../../../core/services/reclamo';
import { Categoria } from '../../../core/models/categoria.model';
import { ReclamoRequest } from '../../../core/models/reclamoRequest.model';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-registrar',
  styleUrl: './registrar.css',
  templateUrl: './registrar.html',
})
export class Registrar implements OnInit{
  formularioReclamo: FormGroup;
  categorias = signal<Categoria[]>([]);
  enviando = false;

  constructor(
    private fb: FormBuilder, 
    private categoriaService : CategoriaService,
    private reclamoService: ReclamoService)
    {

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
    if(this.formularioReclamo.invalid){
      this.formularioReclamo.markAllAsTouched();
      return;
    }
    const payLoad: ReclamoRequest = {
      ...this.formularioReclamo.value,
      tipoReclamo: Number(this.formularioReclamo.value.tipoReclamo)
    }
    this.reclamoService.crear(payLoad).subscribe({
      next: (r) => {
        this.enviando = false;
        alert(`Reclamo registrado. Tu código es: ${r.idReclamo}`),
        this.formularioReclamo.reset();
      },
      error: (err) => {
        this.enviando = false;
        alert(err.error?.mensaje ?? 'No se pudo registrar el reclamo')
      }
    })
  }
}
