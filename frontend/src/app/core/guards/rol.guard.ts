import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

/** Cualquier usuario con sesión vigente. */
export const authGuard: CanActivateFn = () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    return auth.haySesionVigente() ? true : router.createUrlTree(['/login']);
};

/** Solo el rol indicado (1 = Administrador, 2 = Funcionario). */
export const rolGuard = (idRolPermitido: number): CanActivateFn => () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    if (!auth.haySesionVigente()) {
    auth.cerrarSesion();
    return router.createUrlTree(['/login']);
    }
    return auth.obtenerIdRol() === idRolPermitido ? true : router.createUrlTree(['/login']);
};