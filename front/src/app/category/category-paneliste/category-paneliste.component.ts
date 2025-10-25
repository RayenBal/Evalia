import { Component } from '@angular/core';
import { Category } from '../categoryModel/category';
import { announce } from '../../Announce/AnnounceModel/announce';
import { CommonModule } from '@angular/common';
import { CategoryService } from '../categoryService/category.service';
import { forkJoin } from 'rxjs';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { AnnounceServiceService } from '../../Announce/AnnounceService/announce-service.service';
import { FeedbackListComponent } from '../../feedback/feedback-list/feedback-list.component';
import { FeedbackFormComponent } from '../../feedback/feedback-form/feedback-form.component';
@Component({
  selector: 'app-category-paneliste',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './category-paneliste.component.html',
  styleUrl: './category-paneliste.component.css'
})
export class CategoryPanelisteComponent {



  category?: Category;
  announces: announce[] = [];
  loading = true;
  error?: string;
//opened: Record<string, boolean> = {};dans imports FeedbackListComponent
  constructor(
    private route: ActivatedRoute,
    private categoryService: CategoryService,
    private announceService: AnnounceServiceService   // ← pour supprimer/URLs
  ) {}

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
  }

  getImageUrl(fileName?: string): string {
    return fileName
      ? `http://localhost:8081/Announcement/downloadannounce/${fileName}`
      : 'assets/no-image.png';
  }

  

}
