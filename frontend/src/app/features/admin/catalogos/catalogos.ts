import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';

import { AreaService } from '../../../core/services/area';
import { RolService } from '../../../core/services/rol';
import { CategoriaService } from '../../../core/services/categoria';
import { AreaRequest } from '../../../core/models/areaRequest.model';
import { AreaResponse } from '../../../core/models/areaResponse.model';
import { RolRequest } from '../../../core/models/rolRequest.model';
import { RolResponse } from '../../../core/models/rolResponse.model';
import { CategoriaRequest } from '../../../core/models/categoriaRequest.model';
import { CategoriaResponse } from '../../../core/models/categoriaResponse.model';

@Component({
  selector: 'app-catalogos',
  imports: [FormsModule, SidebarAdmin, CampanaNotificaciones],
  styleUrl: './catalogos.css',
  templateUrl: './catalogos.html',
})
export class Catalogos implements OnInit {
  private rolService = inject(RolService);
  private areaService = inject(AreaService);
  private categoriaService = inject(CategoriaService);

  // ---- Roles ----
  roles = signal<RolResponse[]>([]);
  rolForm: RolRequest = { nombre: '', descripcion: '', permisos: '' };
  rolEditandoId: number | null = null;

  // ---- Áreas ----
  areas = signal<AreaResponse[]>([]);
  areaForm: AreaRequest = { nombre: '', descripcion: '' };
  areaEditandoId: number | null = null;

  // ---- Categorías ----
  categorias = signal<CategoriaResponse[]>([]);
  categoriaForm: CategoriaRequest = { nombre: '', descripcion: '' };
  categoriaEditandoId: number | null = null;

  ngOnInit(): void {
    this.cargarRoles();
    this.cargarAreas();
    this.cargarCategorias();
  }

  // ================= ROLES =================
  cargarRoles(): void {
    this.rolService.listar().subscribe({
      next: data => this.roles.set(data),
      error: err => console.error('Error al listar roles', err)
    });
  }

  guardarRol(): void {
    if (!this.rolForm.nombre.trim()) return;

    const peticion = this.rolEditandoId !== null
      ? this.rolService.actualizar(this.rolEditandoId, this.rolForm)
      : this.rolService.crear(this.rolForm);

    peticion.subscribe({
      next: () => {
        this.cancelarRol();
        this.cargarRoles();
      },
      error: err => console.error('Error al guardar rol', err)
    });
  }

  editarRol(rol: RolResponse): void {
    this.rolEditandoId = rol.idRol;
    this.rolForm = {
      nombre: rol.nombre,
      descripcion: rol.descripcion,
      permisos: rol.permisos
    };
  }

  eliminarRol(id: number): void {
    if (!confirm('¿Eliminar este rol?')) return;
    this.rolService.eliminar(id).subscribe({
      next: () => this.cargarRoles(),
      error: err => console.error('Error al eliminar rol', err)
    });
  }

  cancelarRol(): void {
    this.rolEditandoId = null;
    this.rolForm = { nombre: '', descripcion: '', permisos: '' };
  }

  // ================= ÁREAS =================
  cargarAreas(): void {
    this.areaService.listar().subscribe({
      next: data => this.areas.set(data),
      error: err => console.error('Error al listar áreas', err)
    });
  }

  guardarArea(): void {
    if (!this.areaForm.nombre.trim()) return;

    const peticion = this.areaEditandoId !== null
      ? this.areaService.actualizar(this.areaEditandoId, this.areaForm)
      : this.areaService.crear(this.areaForm);

    peticion.subscribe({
      next: () => {
        this.cancelarArea();
        this.cargarAreas();
      },
      error: err => console.error('Error al guardar área', err)
    });
  }

  editarArea(area: AreaResponse): void {
    this.areaEditandoId = area.idArea;
    this.areaForm = { nombre: area.nombre, descripcion: area.descripcion };
  }

  eliminarArea(id: number): void {
    if (!confirm('¿Eliminar esta área?')) return;
    this.areaService.eliminar(id).subscribe({
      next: () => this.cargarAreas(),
      error: err => console.error('Error al eliminar área', err)
    });
  }

  cancelarArea(): void {
    this.areaEditandoId = null;
    this.areaForm = { nombre: '', descripcion: '' };
  }

  // ================= CATEGORÍAS =================
  cargarCategorias(): void {
    this.categoriaService.listar().subscribe({
      next: data => this.categorias.set(data),
      error: err => console.error('Error al listar categorías', err)
    });
  }

  guardarCategoria(): void {
    if (!this.categoriaForm.nombre.trim()) return;

    const peticion = this.categoriaEditandoId !== null
      ? this.categoriaService.actualizar(this.categoriaEditandoId, this.categoriaForm)
      : this.categoriaService.crear(this.categoriaForm);

    peticion.subscribe({
      next: () => {
        this.cancelarCategoria();
        this.cargarCategorias();
      },
      error: err => console.error('Error al guardar categoría', err)
    });
  }

  editarCategoria(categoria: CategoriaResponse): void {
    this.categoriaEditandoId = categoria.idCategoria;
    this.categoriaForm = {
      nombre: categoria.nombre,
      descripcion: categoria.descripcion ?? ''
    };
  }

  eliminarCategoria(id: number): void {
    if (!confirm('¿Eliminar esta categoría?')) return;
    this.categoriaService.eliminar(id).subscribe({
      next: () => this.cargarCategorias(),
      error: err => console.error('Error al eliminar categoría', err)
    });
  }

  cancelarCategoria(): void {
    this.categoriaEditandoId = null;
    this.categoriaForm = { nombre: '', descripcion: '' };
  }
}