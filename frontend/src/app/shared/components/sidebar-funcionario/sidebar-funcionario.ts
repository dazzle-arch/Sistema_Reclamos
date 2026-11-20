import { Component, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-sidebar-funcionario',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  templateUrl: './sidebar-funcionario.html',
  styleUrl: './sidebar-funcionario.css'
})
export class SidebarFuncionario implements OnInit {

  nombreUsuario = 'Funcionario';
  rolUsuario = 'Funcionario';
  iniciales = 'FN';

  constructor(private authService: AuthService) {}

  ngOnInit(): void {
    this.nombreUsuario = this.authService.obtenerNombreUsuario();
    this.rolUsuario = this.authService.obtenerNombreRol();
    this.iniciales = this.generarIniciales(this.nombreUsuario);
  }

  private generarIniciales(nombre: string): string {
    // Elimina textos entre paréntesis como "(Obras Públicas)" y divide por espacios o guiones bajos
    const limpio = nombre.replace(/\(.*?\)/g, '').trim();
    const partes = limpio.split(/[\s_]+/).filter(Boolean);

    if (partes.length >= 2) {
      return (partes[0][0] + partes[1][0]).toUpperCase();
    }
    return limpio.substring(0, 2).toUpperCase() || 'FN';
  }
}
