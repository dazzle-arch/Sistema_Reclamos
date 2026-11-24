import { Component, OnInit } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { DatePipe } from '@angular/common';
import { ConsultaPublicaService } from '../../../core/services/consulta-publica';

@Component({
  imports: [RouterLink, DatePipe],
  selector: 'app-detalle-tramite',
  styleUrl: './detalle-tramite.css',
  templateUrl: './detalle-tramite.html',
})
export class DetalleTramite implements OnInit {
  constructor(public consulta: ConsultaPublicaService, private router: Router) {}

  ngOnInit(): void {
    // Si se entra directo a la URL, no hay datos: se vuelve a la consulta
    if (!this.consulta.resultado()) {
      this.router.navigate(['/consultar-estado']);
    }
  }
}