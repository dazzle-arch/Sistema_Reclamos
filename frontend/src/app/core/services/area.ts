import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable} from 'rxjs';
import { Area } from '../models/area.model';
import { environment } from '../../../environments/environment.development';


@Injectable ({
    providedIn: 'root'
})
export class AreaService {
    private apiUrl = `${environment.apiUrl}/areas`;

    constructor(private http: HttpClient){}

    listar(): Observable<Area[]> {
        return this.http.get<Area[]>(this.apiUrl)
    }

    crear(area: Area): Observable<Area> {
        return this.http.post<Area>(this.apiUrl, area);
    }
}
