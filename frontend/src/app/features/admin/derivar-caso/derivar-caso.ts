import { Component } from '@angular/core';
import { SidebarAdmin } from '../../../shared/components/sidebar-admin/sidebar-admin';
import { RouterLink } from '@angular/router';

@Component({
  imports: [SidebarAdmin, RouterLink],
  selector: 'app-derivar-caso',
  styleUrl: './derivar-caso.css',
  templateUrl: './derivar-caso.html',
})
export class DerivarCaso {}
