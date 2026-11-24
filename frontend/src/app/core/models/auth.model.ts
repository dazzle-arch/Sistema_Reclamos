export interface LoginRequest {
    nombreUsuario: string;
    contrasena: string;
}

export interface AuthResponse {
    token: string;
    nombreUsuario: string;
    idRol: number;
    idArea: number;
    idUsuario: number;
}