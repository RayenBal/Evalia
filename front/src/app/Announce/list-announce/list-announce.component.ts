import { Component, OnInit, OnDestroy } from '@angular/core';
import { AnnounceServiceService } from '../AnnounceService/announce-service.service';
import { announce } from '../AnnounceModel/announce';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth-service/auth.service';
import { Router,RouterModule } from '@angular/router';

@Component({
  selector: 'app-list-announce',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './list-announce.component.html',
  styleUrl: './list-announce.component.css'
})
export class ListAnnounceComponent implements OnInit, OnDestroy {
  announces: announce[] = [];
  currentIndex: number = 0;
  baseUrl: string = 'http://localhost:8081/Announcement';
  
  // Variables pour le drag/swipe
  isDragging: boolean = false;
  startX: number = 0;
  currentTranslate: number = 0;
  prevTranslate: number = 0;
  animationID: number = 0;
  slideWidth: number = 900; // Largeur fixe de chaque slide
  
  constructor(private announceService: AnnounceServiceService, public router:Router ,public auth: AuthService, private announceSvc : AnnounceServiceService) {}
selectedId?: string;
  ngOnInit(): void {
    this.announceService.getAnnounceList().subscribe(data => {
      this.announces = data;
    });
    this.updateSlideWidth();
    window.addEventListener('resize', () => this.updateSlideWidth());
    this.announceSvc.getMyAnnounces().subscribe(list => {
    this.selectedId = list?.[0]?.idAnnouncement ?? undefined;
  });
  }

  updateSlideWidth(): void {
    const width = window.innerWidth;
    if (width <= 768) {
      this.slideWidth = width - 32; // 2rem de padding
    } else if (width <= 1200) {
      this.slideWidth = 700;
    } else {
      this.slideWidth = 900;
    }
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
        if (this.currentIndex >= this.announces.length && this.currentIndex > 0) {
          this.currentIndex = this.announces.length - 1;
        }
      });
    }
  }

  // Navigation avec boutons
  nextAnnounce(): void {
    if (this.currentIndex < this.announces.length - 1) {
      this.currentIndex++;
    } else {
      this.currentIndex = 0;
    }
  }

  previousAnnounce(): void {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    } else {
      this.currentIndex = this.announces.length - 1;
    }
  }

  goToSlide(index: number): void {
    this.currentIndex = index;
  }

  // Gestion du drag/swipe - Souris
  onDragStart(event: MouseEvent): void {
    this.startDrag(event.clientX);
    event.preventDefault();
  }

  onDragMove(event: MouseEvent): void {
    if (this.isDragging) {
      this.drag(event.clientX);
    }
  }

  onDragEnd(): void {
    this.endDrag();
  }

  // Gestion du drag/swipe - Tactile
  onTouchStart(event: TouchEvent): void {
    this.startDrag(event.touches[0].clientX);
  }

  onTouchMove(event: TouchEvent): void {
    if (this.isDragging) {
      this.drag(event.touches[0].clientX);
    }
  }

  onTouchEnd(): void {
    this.endDrag();
  }

  // Logique commune de drag
  private startDrag(clientX: number): void {
    this.isDragging = true;
    this.startX = clientX;
    this.prevTranslate = -this.currentIndex * this.slideWidth;
    this.currentTranslate = this.prevTranslate;
  }

  private drag(clientX: number): void {
    if (!this.isDragging) return;
    
    const currentPosition = clientX;
    const diff = currentPosition - this.startX;
    this.currentTranslate = this.prevTranslate + diff;
    
    // Limiter le drag aux bornes
    const maxTranslate = 0;
    const minTranslate = -(this.announces.length - 1) * this.slideWidth;
    this.currentTranslate = Math.max(minTranslate, Math.min(maxTranslate, this.currentTranslate));
  }

  private endDrag(): void {
    if (!this.isDragging) return;
    this.isDragging = false;
    
    const movedBy = this.currentTranslate - this.prevTranslate;
    
    // Si le mouvement est suffisant (>100px ou 1/3 de la largeur)
    const threshold = this.slideWidth / 3;
    if (movedBy < -threshold && this.currentIndex < this.announces.length - 1) {
      this.currentIndex++;
    } else if (movedBy > threshold && this.currentIndex > 0) {
      this.currentIndex--;
    }
    
    // Reset la position
    this.currentTranslate = -this.currentIndex * this.slideWidth;
  }

  getTransform(): string {
    if (this.isDragging) {
      return `translateX(${this.currentTranslate}px)`;
    }
    return `translateX(-${this.currentIndex * this.slideWidth}px)`;
  }

  ngOnDestroy(): void {
    window.removeEventListener('resize', () => this.updateSlideWidth());
  }

     logout() {
    this.auth.clearToken();
    this.router.navigate(['/login']);
  }
  setSelected(a: announce) {
  this.selectedId = a.idAnnouncement;
}
}
