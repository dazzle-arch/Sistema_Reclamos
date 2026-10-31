import { Component } from '@angular/core'; 
import { RouterLink } from '@angular/router';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';

@Component({
  imports: [SidebarAdmin],
  selector: 'app-notificaciones',
  styleUrl: './notificaciones.css',
  templateUrl: './notificaciones.html',
})
export class Notificaciones {}
