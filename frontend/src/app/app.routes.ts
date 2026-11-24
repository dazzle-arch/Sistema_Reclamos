import { Routes } from '@angular/router';
import { Home } from './features/public/home/home'
import { Login } from './features/auth/login/login'
import { Registrar} from './features/ciudadano/registrar/registrar'
import { ConsultarEstado } from './features/ciudadano/consultar-estado/consultar-estado'
import { DetalleTramite } from './features/ciudadano/detalle-tramite/detalle-tramite'; 
import { PanelControl } from './features/admin/panel-control/panel-control';
import { BandejaCasos } from './features/admin/bandeja-casos/bandeja-casos';
import { Reportes } from './features/admin/reportes/reportes';
import { Usuarios } from './features/admin/usuarios/usuarios';
import { BandejaCasos as BandejaCasosFuncionario } from './features/funcionario/bandeja-casos/bandeja-casos';
import { AtenderCaso } from './features/funcionario/atender-caso/atender-caso';
import { DetalleReclamo } from './features/admin/detalle-reclamo/detalle-reclamo';
import { DerivarCaso } from './features/admin/derivar-caso/derivar-caso';
import { FormularioUsuario } from './features/admin/formulario-usuario/formulario-usuario';
import { Notificaciones } from './features/admin/notificaciones/notificaciones';
import { Catalogos } from './features/admin/catalogos/catalogos'
import { CasosAtendidos } from './features/funcionario/casos-atendidos/casos-atendidos';
import { authGuard, rolGuard } from './core/guards/rol.guard';

export const routes: Routes = [

    {path: '', component: Home},
    {path: 'home', component: Home},
    {path: 'login', component: Login},
    {path: 'registrar', component: Registrar},
    {path: 'consultar-estado', component: ConsultarEstado},
    {path: 'detalle-tramite', component: DetalleTramite},

    {path: 'admin/panel-control', component: PanelControl, canActivate: [rolGuard(1)]},
    {path: 'admin/bandeja-casos', component: BandejaCasos, canActivate: [rolGuard(1)]},
    {path: 'admin/reportes', component: Reportes, canActivate: [rolGuard(1)]},
    {path: 'admin/usuarios', component: Usuarios, canActivate: [rolGuard(1)]},
    {path: 'admin/detalle-reclamo/:id', component: DetalleReclamo, canActivate: [rolGuard(1)]},
    {path: 'admin/derivar-caso/:id', component: DerivarCaso, canActivate: [rolGuard(1)]},
    {path: 'admin/formulario-usuario', component: FormularioUsuario, canActivate: [rolGuard(1)]},
    {path: 'admin/formulario-usuario/:id', component: FormularioUsuario, canActivate: [rolGuard(1)]},
    {path: 'admin/catalogos', component: Catalogos, canActivate: [rolGuard(1)]},

    {path: 'funcionario/bandeja-casos', component: BandejaCasosFuncionario, canActivate: [rolGuard(2)]},
    {path: 'funcionario/atender-caso/:id', component: AtenderCaso, canActivate: [rolGuard(2)]},
    {path: 'funcionario/casos-atendidos', component: CasosAtendidos, canActivate: [rolGuard(2)]},

    {path: 'notificaciones', component: Notificaciones, canActivate: [authGuard]},
];

