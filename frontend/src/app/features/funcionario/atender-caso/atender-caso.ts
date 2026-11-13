import { Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CampanaNotificaciones } from '../../../shared/components/campana-notificaciones/campana-notificaciones';
import { SidebarFuncionario } from '../../../shared/components/sidebar-funcionario/sidebar-funcionario';
@Component({
  imports: [RouterLink, CampanaNotificaciones, SidebarFuncionario],
  selector: 'app-atender-caso',
  styleUrl: './atender-caso.css',
  templateUrl: './atender-caso.html',
})
export class AtenderCaso {
  idCaso: String | null = null;

  constructor(private route: ActivatedRoute){
    this.idCaso = this.route.snapshot.paramMap.get('id');
  }
}
