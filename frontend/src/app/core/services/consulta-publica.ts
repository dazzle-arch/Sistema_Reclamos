import { Injectable, signal } from '@angular/core';
import { ConsultaPublica } from '../models/consultaPublica.model';

@Injectable({ providedIn: 'root' })
export class ConsultaPublicaService {
    resultado = signal<ConsultaPublica | null>(null);
}