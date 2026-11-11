import { Component } from '@angular/core';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';

@Component({
  imports: [SidebarAdmin, CampanaNotificaciones],
  selector: 'app-catalogos',
  styleUrl: './catalogos.css',
  templateUrl: './catalogos.html',
})
export class Catalogos {}
