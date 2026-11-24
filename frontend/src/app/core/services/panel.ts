import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResumenPanel } from '../models/resumenPanel.model';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class PanelService {
    private apiUrl = `${environment.apiUrl}/panel`;

    constructor(private http: HttpClient) {}

    resumen(): Observable<ResumenPanel> {
        return this.http.get<ResumenPanel>(`${this.apiUrl}/resumen`);
    }
}