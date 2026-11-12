import { Component } from '@angular/core';
import { SidebarFuncionario } from '../../../shared/components/sidebar-funcionario/sidebar-funcionario';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';

@Component({
  imports: [SidebarFuncionario, CampanaNotificaciones],
  selector: 'app-casos-atendidos',
  styleUrl: './casos-atendidos.css',
  templateUrl: './casos-atendidos.html',
})
export class CasosAtendidos {}
