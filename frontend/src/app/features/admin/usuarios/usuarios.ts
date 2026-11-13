import { Component, OnInit, signal, computed} from '@angular/core';
import { RouterLink } from '@angular/router';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
import { UsuarioService } from '../../../core/services/usuario';
import { UsuarioResponse } from '../../../core/models/usuarioResponse.model';

@Component({
  imports: [SidebarAdmin, CampanaNotificaciones, RouterLink],
  selector: 'app-usuarios',
  styleUrl: './usuarios.css',
  templateUrl: './usuarios.html',
})
export class Usuarios {
  usuarios = signal<UsuarioResponse[]>([]);
  cargando = signal(true);
  error = signal(false);
  busqueda = signal('');
  orden = signal("nombre");

  filtrados = computed(() => {
    const texto = this.busqueda().toLowerCase().trim();
    const lista = this.usuarios().filter(u =>
      u.nombres.toLowerCase().includes(texto) || u.nombreUsuario.toLowerCase().includes(texto))
    return [...lista].sort((a,b) => 
      this.orden() === 'cargo'
      ? a.nombreRol.localeCompare(b.nombreRol)
      : a.nombres.localeCompare(b.nombres))
  })

  constructor(private usuarioService: UsuarioService) {}

  ngOnInit(): void {
    this.usuarioService.listar().subscribe({
      next: (data) => {
        this.usuarios.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set(true);
        this.cargando.set(false);
      }
    });
  }

  cambiarEstado(u: UsuarioResponse): void {
    const accion = u.estado ? 'desactivar' : 'activar';
    if (!confirm(`¿Seguro que deseas ${accion} a ${u.nombres}?`)) return;

    this.usuarioService.cambiarEstado(u.idUsuario, !u.estado).subscribe({
      next: (actualizado) => this.usuarios.update(lista =>
        lista.map(x => x.idUsuario === actualizado.idUsuario ? actualizado : x)),
      error: (err) => alert(err.error?.mensaje ?? 'No se pudo cambiar el estado')
    });
  }


}
