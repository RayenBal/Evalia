import { Component, OnInit } from '@angular/core';
import { AnnounceServiceService } from '../AnnounceService/announce-service.service';
import { announce } from '../AnnounceModel/announce';
import { CommonModule } from '@angular/common';
import { Router,RouterModule } from '@angular/router';
import { AuthService } from '../../auth/auth-service/auth.service';
@Component({
  selector: 'app-anounces',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './anounces.component.html',
  styleUrl: './anounces.component.css'
})
export class AnouncesComponent implements OnInit{
 announces: announce[] = [];
baseUrl: string = 'http://localhost:8081/Announcement';
  constructor(private announceService: AnnounceServiceService, public router:Router, public auth: AuthService) {}
selectedId?: string;
  ngOnInit(): void {
    this.announceService.getAnnounceList().subscribe(data => {
      this.announces = data;
    });
  }

  loadAnnounces(): void {
    this.announceService.getAnnounceList().subscribe({
      next: (data) => this.announces = data,
      error: (err) => console.error('Erreur chargement annonces :', err)
    });
  }
  getImageUrl(fileName: string | undefined): string {
    return fileName ? `http://localhost:8081/Announcement/downloadannounce/${fileName}` : '';
  }

  deleteAnnounce(id: string): void {
    if (confirm("Voulez-vous vraiment supprimer cette annonce ?")) {
      this.announceService.deleteAnnounce(id).subscribe(() => {
        this.announces = this.announces.filter(a => a.idAnnouncement !== id);
      });
    }

    
  }

  logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }

 
}


