import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule,Router } from '@angular/router';
import { RecompensesService,RewardSummary } from '../../recompenses/recompensesService/recompenses.service';

import { Category } from '../../category/categoryModel/category'; 
import { CategoryService } from '../../category/categoryService/category.service'; 
import { catchError, map } from 'rxjs/operators';
import { forkJoin, of } from 'rxjs';
import { announce } from '../../Announce/AnnounceModel/announce';
import { AnnounceServiceService } from '../../Announce/AnnounceService/announce-service.service';
import { AuthService } from '../../auth/auth-service/auth.service';
@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css'
})
export class HomeComponent implements OnInit{
today = new Date();

  categoryList: Category[] = [];
  announces: announce[] = [];
  rewardsById: Record<string, RewardSummary> = {};
 selectedCategory: Category | null = null;
  announcesByCategory: announce[] = [];
  constructor(
    private categoryService: CategoryService,
    private announceService: AnnounceServiceService,
        private router: Router,
            private rewards: RecompensesService

        //public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.loadCategories();
    this.loadAnnounces();
  }

  loadCategories(): void {
    this.categoryService.getcategorieList().subscribe({
      next: (data) => (this.categoryList = data),
      error: (err) => console.error('Erreur lors du chargement des catégories', err),
    });
  }

  loadAnnounces(): void {
    this.announceService.getAnnounceList().subscribe({
      next: data => {
        this.announces = data ?? [];
        // charge les récompenses pour la liste des annonces affichées par défaut
        this.loadRewardsFor(this.announces);
      },
      error: err => console.error('Erreur chargement annonces', err),
    });
  }

  getImageUrl(fileName?: string): string {
    return fileName
      ? `http://localhost:8081/Announcement/downloadannounce/${fileName}`
      : 'assets/no-image.png';
  }

  deleteAnnounce(id: string): void {
    if (!confirm('Voulez-vous vraiment supprimer cette annonce ?')) return;
    this.announceService.deleteAnnounce(id).subscribe(() => {
      this.announces = this.announces.filter(a => a.idAnnouncement !== id);
    });
  }
  
 /* logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }*/

get displayedAnnounces(): announce[] {
  return this.selectedCategory ? this.announcesByCategory : this.announces;
}

private toNumber(v: unknown): number | null {
  const n = typeof v === 'string' ? Number(v) : (typeof v === 'number' ? v : NaN);
  return Number.isFinite(n) ? n : null;
}

selectCategory(cat: Category): void {
  const id = this.toNumber((cat as any).idcategory);
  if (id == null) { console.warn('idcategory invalide:', (cat as any).idcategory); return; }

  if (this.selectedCategory && this.toNumber((this.selectedCategory as any).idcategory) === id) {
    this.clearCategory();
    return;
  }
  this.selectedCategory = cat;

  this.announceService.getByCategory(id).subscribe({
    next: list => {this.announcesByCategory = list ?? [];
      this.loadRewardsFor(this.announcesByCategory); 
    },
    error: _ => {
      this.announcesByCategory = this.announces
        .filter(a => this.toNumber((a as any).category?.idcategory) === id);
        this.loadRewardsFor(this.announcesByCategory);
    }
  });
}

clearCategory(): void {
  this.selectedCategory = null;
  this.announcesByCategory = [];
    this.loadRewardsFor(this.announces);
}
  // ---------- récompenses ----------
  private loadRewardsFor(list: announce[]) {
    const calls = (list ?? [])
      .filter(a => !!a?.idAnnouncement)
      .map(a =>
        this.rewards.getForAnnouncementPublic(a.idAnnouncement!).pipe(
          map(arr => [a.idAnnouncement!, this.rewards.summarize(arr)] as const),
          catchError(() => of([a.idAnnouncement!, { text: '', items: [] }] as const))
        )
      );

    if (!calls.length) { this.rewardsById = {}; return; }

    forkJoin(calls).subscribe(entries => {
      this.rewardsById = Object.fromEntries(entries);
    });
  }



}