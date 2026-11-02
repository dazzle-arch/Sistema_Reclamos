import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
@Component({
  imports: [SidebarAdmin, CampanaNotificaciones, RouterLink],
  selector: 'app-usuarios',
  styleUrl: './usuarios.css',
  templateUrl: './usuarios.html',
})
export class Usuarios {}
