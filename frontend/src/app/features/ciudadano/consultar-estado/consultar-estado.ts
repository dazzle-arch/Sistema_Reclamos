import { Component, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ReclamoService } from '../../../core/services/reclamo';
import { ConsultaPublicaService } from '../../../core/services/consulta-publica';
import { ConsultaPublica } from '../../../core/models/consultaPublica.model';

@Component({
  imports: [RouterLink, ReactiveFormsModule],
  selector: 'app-consultar-estado',
  styleUrl: './consultar-estado.css',
  templateUrl: './consultar-estado.html',
})
export class ConsultarEstado {
  form: FormGroup;
  resultado = signal<ConsultaPublica | null>(null);
  error = signal('');
  buscando = signal(false);

  readonly pasos = ['Registrado', 'Derivado', 'Atendido', 'Cerrado'];

  constructor(
    private fb: FormBuilder,
    private reclamoService: ReclamoService,
    private consultaService: ConsultaPublicaService,
    private router: Router
  ) {
    this.form = this.fb.group({
      codigo: ['', [Validators.required, Validators.pattern('^[0-9]+$')]],
      dni: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]]
    });
  }

  /** 1 = registrado ... 4 = cerrado */
  get pasoActual(): number {
    switch (this.resultado()?.estado) {
      case 'EN_PROCESO': return 2;
      case 'RESUELTO': return 3;
      case 'CERRADO': return 4;
      default: return 1;
    }
  }

  consultar(): void {
    if (this.form.invalid || this.buscando()) {
      this.form.markAllAsTouched();
      return;
    }
    this.buscando.set(true);
    this.error.set('');
    this.resultado.set(null);

    const { codigo, dni } = this.form.value;
    this.reclamoService.consultar(Number(codigo), String(dni).trim()).subscribe({
      next: (r) => {
        this.buscando.set(false);
        this.resultado.set(r);
        this.consultaService.resultado.set(r);
      },
      error: (err) => {
        this.buscando.set(false);
        this.error.set(err.status === 429
          ? 'Demasiadas consultas. Intente nuevamente en un minuto.'
          : err.error?.mensaje ?? 'No se pudo realizar la consulta.');
      }
    });
  }

  verDetalle(): void {
    this.router.navigate(['/detalle-tramite']);
  }
}