import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DocumentoResponse } from '../models/documentoResponse.model';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class DocumentoService {
    private apiUrl = `${environment.apiUrl}/documentos`;

    constructor(private http: HttpClient) {}

    subirEvidencia(codigo: number, dni: string, archivo: File): Observable<DocumentoResponse> {
        const datos = new FormData();
        datos.append('codigo', String(codigo));
        datos.append('dni', dni);
        datos.append('archivo', archivo);
        return this.http.post<DocumentoResponse>(`${this.apiUrl}/evidencia`, datos);
    }

    listarPorReclamo(idReclamo: number): Observable<DocumentoResponse[]> {
        return this.http.get<DocumentoResponse[]>(`${this.apiUrl}/reclamo/${idReclamo}`);
    }

    /** Descarga con el token (por eso va por HttpClient y no con un enlace directo). */
    descargar(doc: DocumentoResponse, alError: () => void): void {
        this.http.get(`${this.apiUrl}/${doc.idDocumento}/archivo`, { responseType: 'blob' }).subscribe({
            next: (blob) => {
                const url = URL.createObjectURL(blob);
                const a = document.createElement('a');
                a.href = url;
                a.download = doc.nombreArchivo;
                a.click();
                URL.revokeObjectURL(url);
            },
            error: alError
        });
    }
}