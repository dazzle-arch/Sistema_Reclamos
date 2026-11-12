import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';
import { AreaResponse } from '../models/areaResponse.model';
import { AreaRequest } from '../models/areaRequest.model';
import { environment } from '../../../environments/environment.development';


@Injectable ({
    providedIn: 'root'
})
export class AreaService {
    private apiUrl = `${environment.apiUrl}/areas`;

    constructor(private http: HttpClient) {}

    listar(): Observable<AreaResponse[]> {
        return this.http.get<AreaResponse[]>(this.apiUrl);
    }

    crear(area: AreaRequest): Observable<AreaResponse> {
        return this.http.post<AreaResponse>(this.apiUrl, area);
    }

    actualizar(id: number, area: AreaRequest): Observable<AreaResponse> {
        return this.http.put<AreaResponse>(`${this.apiUrl}/${id}`, area);
    }

    eliminar(id: number): Observable<void> {
        return this.http.delete<void>(`${this.apiUrl}/${id}`);
    }
}
