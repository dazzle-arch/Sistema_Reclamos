import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';  

@Component({
  imports: [SidebarAdmin, RouterLink],
  selector: 'app-detalle-reclamo',
  styleUrl: './detalle-reclamo.css',
  templateUrl: './detalle-reclamo.html',
})
export class DetalleReclamo {}
