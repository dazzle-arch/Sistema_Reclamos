import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SidebarFuncionario } from '../../../shared/components/sidebar-funcionario/sidebar-funcionario';

@Component({
  imports: [RouterLink, SidebarFuncionario],
  selector: 'app-bandeja-casos',
  styleUrl: './bandeja-casos.css',
  templateUrl: './bandeja-casos.html',
})
export class BandejaCasos {}
