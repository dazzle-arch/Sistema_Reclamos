import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const token = authService.obtenerToken();

    const peticion = token
        ? req.clone({ headers: req.headers.set('Authorization', `Bearer ${token}`) })
        : req;

    return next(peticion).pipe(
        catchError((err: HttpErrorResponse) => {
            // 401 fuera del login = token vencido o usuario desactivado
            if (err.status === 401 && token && !req.url.includes('/auth/login')) {
                authService.cerrarSesion();
                router.navigate(['/login']);
            }
            return throwError(() => err);
        })
    );
};