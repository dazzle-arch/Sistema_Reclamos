import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';
@Component({
  imports: [SidebarAdmin, RouterLink],
  selector: 'app-usuarios',
  styleUrl: './usuarios.css',
  templateUrl: './usuarios.html',
})
export class Usuarios {}
