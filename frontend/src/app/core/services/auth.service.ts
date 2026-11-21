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
            tap(response => {
                this.guardarToken(response.token);
            })
        );
    }

    guardarToken(token: string): void {
        localStorage.setItem(this.TOKEN_KEY, token);
    }

    obtenerToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    cerrarSesion(): void {
        localStorage.removeItem(this.TOKEN_KEY);
    }

    obtenerIdRol(): number | null {
        const token = this.obtenerToken();
        if (!token) return null;

        try {
            const decoded: any = jwtDecode(token);
            return decoded.rol; // Extrae el "claim" de rol que inyectaste en JwtService de Java
        } catch (Error) {
            return null;
        }
    }

    obtenerIdUsuario(): number | null {
        const token = this.obtenerToken();
        if (!token) return null;

        try {
            const decoded: any = jwtDecode(token);
            // Extrae el ID del usuario del payload del token. 
            return decoded.idUsuario || decoded.id;
        } catch (Error) {
            return null;
        }
    }

    obtenerNombreUsuario(): string {
        const token = this.obtenerToken();
        if (!token) return 'Funcionario';

        try {
            const decoded: any = jwtDecode(token);
            // Busca el nombre completo si lo incluiste en el JWT; si no, usa el 'sub' (ej. funcionario_carlos)
            return decoded.nombres || decoded.nombre || decoded.sub || 'Funcionario';
        } catch (Error) {
            return 'Funcionario';
        }
    }

    obtenerNombreRol(): string {
        const idRol = this.obtenerIdRol();
        if (idRol === 1) return 'Administrador';
        if (idRol === 2) return 'Funcionario';
        return 'Usuario';
    }
}