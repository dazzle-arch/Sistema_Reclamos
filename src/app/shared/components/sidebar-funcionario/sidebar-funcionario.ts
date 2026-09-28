import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  imports: [RouterLink, RouterLinkActive],
  selector: 'app-sidebar-funcionario',
  styleUrl: './sidebar-funcionario.css',
  templateUrl: './sidebar-funcionario.html',
})
export class SidebarFuncionario {}
