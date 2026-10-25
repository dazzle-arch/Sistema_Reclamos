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

export const routes: Routes = [
    {path: '', component: Home},
    {path: 'home', component: Home},
    {path: 'login', component: Login},
    {path: 'registrar', component: Registrar},
    {path: 'consultar-estado', component: ConsultarEstado},
    {path: 'detalle-tramite', component: DetalleTramite},
    {path: 'admin/panel-control', component: PanelControl},
    {path: 'admin/bandeja-casos', component: BandejaCasos },
    {path: 'admin/reportes', component: Reportes },
    {path: 'admin/usuarios', component: Usuarios },
    { path: 'funcionario/bandeja-casos', component: BandejaCasosFuncionario },
    { path: 'funcionario/atender-caso/:id', component: AtenderCaso },

];
