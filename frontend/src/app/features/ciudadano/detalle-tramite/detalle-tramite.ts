import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DatePipe, UpperCasePipe, TitleCasePipe } from '@angular/common';

@Component({
  
  imports: [RouterLink, DatePipe, UpperCasePipe, TitleCasePipe], 
  selector: 'app-detalle-tramite',
  styleUrl: './detalle-tramite.css',
  templateUrl: './detalle-tramite.html',
})
export class DetalleTramite {
  //simulacion
  datosCrudos = {
    departamento: 'limpieza publica y ornato',
    fechaApertura: '2026-09-24T10:00:00Z',
    fechaEstimada: '2026-10-15T18:00:00Z'
  };

  // simulacion 
  historial = [
    { fecha: '2026-09-24T10:00:00Z', accion: 'registro inicial del ciudadano' },
    { fecha: '2026-09-25T08:30:00Z', accion: 'derivado al departamento de limpieza' },
    { fecha: '2026-09-28T14:15:00Z', accion: 'en atencion por el personal' }
  ];
}