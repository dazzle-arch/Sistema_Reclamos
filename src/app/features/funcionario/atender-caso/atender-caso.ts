import { Component } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

@Component({
  imports: [RouterLink],
  selector: 'app-atender-caso',
  styleUrl: './atender-caso.css',
  templateUrl: './atender-caso.html',
})
export class AtenderCaso {
  idCaso: String | null = null;

  constructor(private route: ActivatedRoute){
    this.idCaso = this.route.snapshot.paramMap.get('id');
  }
}
