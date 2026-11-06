import { Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { SeguimientoResponse } from '../models/seguimientoResponse.model'; 

@Injectable({ providedIn: 'root'})
export class SeguimientoService {
    private apiUrl = `${environment.apiUrl}/seguimientos`;

    constructor(private http: HttpClient){}

    listarPorReclamo(idReclamo: number): Observable<SeguimientoResponse[]>{
        return this.http.get<SeguimientoResponse[]>((`${this.apiUrl}/reclamo/${idReclamo}`))
    }
}
