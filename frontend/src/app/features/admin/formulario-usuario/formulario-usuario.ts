import { Component, OnInit, signal } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
import { UsuarioService } from '../../../core/services/usuario';
import { RolService } from '../../../core/services/rol';
import { AreaService } from '../../../core/services/area';
import { RolResponse } from '../../../core/models/rolResponse.model';
import { AreaResponse } from '../../../core/models/areaResponse.model';
import { UsuarioRequest } from '../../../core/models/usuarioRequest.model';

@Component({
  imports: [SidebarAdmin, CampanaNotificaciones, RouterLink, ReactiveFormsModule],
  selector: 'app-formulario-usuario',
  styleUrl: './formulario-usuario.css',
  templateUrl: './formulario-usuario.html',
})
export class FormularioUsuario implements OnInit {
  formulario: FormGroup;
  idUsuario: number | null;
  roles = signal<RolResponse[]>([]);
  areas = signal<AreaResponse[]>([]);
  enviando = signal(false);

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private usuarioService: UsuarioService,
    private rolService: RolService,
    private areaService: AreaService
  ) {
    const id = this.route.snapshot.paramMap.get('id');
    this.idUsuario = id ? Number(id) : null;

    this.formulario = this.fb.group({
      nombres: ['', [Validators.required, Validators.maxLength(120)]],
      nombreUsuario: ['', [Validators.required, Validators.maxLength(60)]],
      correo: ['', [Validators.required, Validators.email, Validators.maxLength(160)]],
      contrasena: ['', this.idUsuario
        ? [Validators.minLength(6)]
        : [Validators.required, Validators.minLength(6)]],
      idRol: ['', Validators.required],
      idArea: ['', Validators.required],
    });
  }

  ngOnInit(): void {
    this.rolService.listar().subscribe({ next: (data) => this.roles.set(data) });
    this.areaService.listar().subscribe({ next: (data) => this.areas.set(data) });

    if (this.idUsuario) {
      this.usuarioService.buscarPorId(this.idUsuario).subscribe({
        next: (u) => this.formulario.patchValue({
          nombres: u.nombres,
          nombreUsuario: u.nombreUsuario,
          correo: u.correo,
          idRol: u.idRol,
          idArea: u.idArea,
        }),
        error: () => this.router.navigate(['/admin/usuarios'])
      });
    } 
  }

  error(campo: string, tipo: string): boolean {
    const control = this.formulario.get(campo);
    return !!control?.errors?.[tipo] && !!control?.touched;
  }

  guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }
    const v = this.formulario.value;
    const payload: UsuarioRequest = {
      nombreUsuario: v.nombreUsuario,
      nombres: v.nombres,
      correo: v.correo,
      contrasena: v.contrasena || undefined,
      idRol: Number(v.idRol),
      idArea: Number(v.idArea),
    };

    this.enviando.set(true);
    const peticion = this.idUsuario
      ? this.usuarioService.actualizar(this.idUsuario, payload)
      : this.usuarioService.crear(payload);

    peticion.subscribe({
      next: () => this.router.navigate(['/admin/usuarios']),
      error: (err) => {
        this.enviando.set(false);
        alert(err.error?.mensaje ?? 'No se pudo guardar el usuario');
      }
    });
  }
}
