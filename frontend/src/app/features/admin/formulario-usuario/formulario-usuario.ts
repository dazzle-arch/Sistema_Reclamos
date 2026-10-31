import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';

@Component({
  imports: [SidebarAdmin, RouterLink],
  selector: 'app-formulario-usuario',
  styleUrl: './formulario-usuario.css',
  templateUrl: './formulario-usuario.html',
})
export class FormularioUsuario {}
