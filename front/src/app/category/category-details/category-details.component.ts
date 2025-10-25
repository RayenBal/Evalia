import { Component } from '@angular/core';
import { Category } from '../categoryModel/category';
import { announce } from '../../Announce/AnnounceModel/announce';
import { CommonModule  ,Location} from '@angular/common';
import { CategoryService } from '../categoryService/category.service';
import { forkJoin } from 'rxjs';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AnnounceServiceService } from '../../Announce/AnnounceService/announce-service.service';
import { FeedbackListComponent } from '../../feedback/feedback-list/feedback-list.component';
import { AuthService } from '../../auth/auth-service/auth.service';
@Component({
  selector: 'app-category-details',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './category-details.component.html',
  styleUrl: './category-details.component.css'
})
export class CategoryDetailsComponent {
  category?: Category;
  announces: announce[] = [];
  loading = true;
  error?: string;
//opened: Record<string, boolean> = {};dans imports FeedbackListComponent
  constructor(
    private route: ActivatedRoute,
    private categoryService: CategoryService,
    private announceService: AnnounceServiceService ,
    private router:Router,
   public auth: AuthService, private loc: Location
   
  ) {}
 get isAnnonceur() { return this.auth.userType === 'Announceur'; }
  get isPaneliste() { return this.auth.userType === 'Paneliste'; }
   selectedId?: string;
  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id')!;
    this.loading = true;

    forkJoin({
      category: this.categoryService.getCategory(id),
      announces: this.categoryService.getAnnouncesByCategory(id)
    }).subscribe({
      next: ({ category, announces }) => {
        this.category = category;
        this.announces = announces || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Erreur chargement détail catégorie', err);
        this.error = 'Impossible de charger cette catégorie.';
        this.loading = false;
      }
    });
    this.announceService.getMyAnnounces().subscribe(list => {
    this.selectedId = list?.[0]?.idAnnouncement ?? undefined;
  })
  }

  getImageUrl(fileName?: string): string {
    return fileName
      ? `http://localhost:8081/Announcement/downloadannounce/${fileName}`
      : 'assets/no-image.png';
  }

  // Optionnel si tu veux supprimer directement depuis la page
  deleteFromCategory(id: string): void {
    if (!confirm('Supprimer cette annonce ?')) return;
    this.announceService.deleteAnnounce(id).subscribe({
      next: () => (this.announces = this.announces.filter(a => a.idAnnouncement !== id)),
      error: (err) => console.error('Erreur suppression annonce :', err)
    });
  }
   /* toggle(a: announce): void {
    const id = a.idAnnouncement!;
    this.opened[id] = !this.opened[id];
  }

  isOpen(a: announce): boolean {
    return !!this.opened[a.idAnnouncement!];
  }*/
  logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }
  setSelected(a: announce) {
  this.selectedId = a.idAnnouncement;
}

 goBack() { this.loc.back(); }
}
