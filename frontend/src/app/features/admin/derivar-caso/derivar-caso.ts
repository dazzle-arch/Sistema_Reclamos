import { Component, OnInit, signal } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
import { ReclamoService } from '../../../core/services/reclamo';
import { AreaService } from '../../../core/services/area';
import { UsuarioService } from '../../../core/services/usuario';
import { ReclamoResponse } from '../../../core/models/reclamoResponse.model';
import { AreaResponse } from '../../../core/models/areaResponse.model';
import { UsuarioResponse } from '../../../core/models/usuarioResponse.model';

@Component({
  imports: [SidebarAdmin, CampanaNotificaciones, RouterLink, FormsModule],
  selector: 'app-derivar-caso',
  styleUrl: './derivar-caso.css',
  templateUrl: './derivar-caso.html',
})
export class DerivarCaso implements OnInit {
  // Temporal: cuando haya login, sale del token
  private readonly ID_USUARIO_DERIVA = 1;

  idReclamo = 0;
  reclamo = signal<ReclamoResponse | null>(null);
  areas = signal<AreaResponse[]>([]);
  funcionarios = signal<UsuarioResponse[]>([]);
  error = signal<string | null>(null);
  enviando = signal(false);

  idAreaSeleccionada: number | null = null;
  idFuncionarioSeleccionado: number | null = null;
  observacion = '';

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private reclamoService: ReclamoService,
    private areaService: AreaService,
    private usuarioService: UsuarioService
  ) {}

  ngOnInit(): void {
    this.idReclamo = Number(this.route.snapshot.paramMap.get('id'));

    this.reclamoService.buscarPorId(this.idReclamo).subscribe({
      next: (r) => this.reclamo.set(r),
      error: () => this.error.set('No se pudo cargar el reclamo')
    });

    this.areaService.listar().subscribe({
      next: (a) => this.areas.set(a),
      error: () => this.error.set('No se pudieron cargar las áreas')
    });
  }

  alCambiarArea(): void {
    this.idFuncionarioSeleccionado = null;
    this.funcionarios.set([]);
    if (this.idAreaSeleccionada === null) return;

    this.usuarioService.listarActivosPorArea(this.idAreaSeleccionada).subscribe({
      next: (u) => this.funcionarios.set(u),
      error: () => this.error.set('No se pudieron cargar los funcionarios')
    });
  }

  confirmar(): void {
    if (this.idAreaSeleccionada === null || this.idFuncionarioSeleccionado === null) {
      this.error.set('Selecciona un área y un funcionario');
      return;
    }

    this.enviando.set(true);
    this.error.set(null);

    this.reclamoService.derivar(this.idReclamo, {
      idArea: this.idAreaSeleccionada,
      idUsuarioAsignado: this.idFuncionarioSeleccionado,
      idUsuarioDeriva: this.ID_USUARIO_DERIVA,
      observacion: this.observacion.trim() || null
    }).subscribe({
      next: () => this.router.navigate(['/admin/detalle-reclamo', this.idReclamo]),
      error: (err) => {
        this.error.set(err.error?.mensaje ?? 'No se pudo derivar el reclamo');
        this.enviando.set(false);
      }
    });
  }
}