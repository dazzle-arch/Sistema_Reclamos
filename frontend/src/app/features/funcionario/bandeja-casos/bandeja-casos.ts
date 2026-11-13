import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SidebarFuncionario } from '../../../shared/components/sidebar-funcionario/sidebar-funcionario';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';

@Component({
  imports: [RouterLink, SidebarFuncionario, CampanaNotificaciones],
  selector: 'app-bandeja-casos',
  styleUrl: './bandeja-casos.css',
  templateUrl: './bandeja-casos.html',
})
export class BandejaCasos {}
