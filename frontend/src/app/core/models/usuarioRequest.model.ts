export interface UsuarioRequest {
    nombreUsuario: string;
    nombres: string;
    correo: string;
    contrasena?: string;
    idRol: number;
    idArea: number;
}