import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';  

@Component({
  imports: [SidebarAdmin, RouterLink],
  selector: 'app-bandeja-casos',
  styleUrl: './bandeja-casos.css',
  templateUrl: './bandeja-casos.html',
})
export class BandejaCasos {}
