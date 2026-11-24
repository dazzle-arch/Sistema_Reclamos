import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment.development';
import { AuthResponse, LoginRequest } from '../models/auth.model';
import { jwtDecode } from 'jwt-decode';

@Injectable({
    providedIn: 'root'
})
export class AuthService {
    private apiUrl = `${environment.apiUrl}/auth`;
    private readonly TOKEN_KEY = 'jwt_token';

    constructor(private http: HttpClient) { }

    login(credentials: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, credentials).pipe(
            tap(response => this.guardarToken(response.token))
        );
    }

    guardarToken(token: string): void {
        sessionStorage.setItem(this.TOKEN_KEY, token);
    }

    obtenerToken(): string | null {
        return sessionStorage.getItem(this.TOKEN_KEY);
    }

    cerrarSesion(): void {
        sessionStorage.removeItem(this.TOKEN_KEY);
    }

    /** Solo comodidad de interfaz: la validación real la hace el backend. */
    haySesionVigente(): boolean {
        const token = this.obtenerToken();
        if (!token) return false;
        try {
            const decoded: any = jwtDecode(token);
            return !decoded.exp || decoded.exp * 1000 > Date.now();
        } catch {
            return false;
        }
    }

    private leerClaim(nombre: string): any {
        const token = this.obtenerToken();
        if (!token) return null;
        try {
            return (jwtDecode(token) as any)[nombre] ?? null;
        } catch {
            return null;
        }
    }

    obtenerIdRol(): number | null {
        return this.leerClaim('rol');
    }

    obtenerIdUsuario(): number | null {
        return this.leerClaim('idUsuario');
    }

    obtenerNombreUsuario(): string {
        return this.leerClaim('nombres') || this.leerClaim('sub') || 'Funcionario';
    }

    obtenerNombreRol(): string {
        const idRol = this.obtenerIdRol();
        if (idRol === 1) return 'Administrador';
        if (idRol === 2) return 'Funcionario';
        return 'Usuario';
    }
}