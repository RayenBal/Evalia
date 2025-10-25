import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule,Router } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { Category } from '../../../category/categoryModel/category';
import { CategoryService } from '../../../category/categoryService/category.service';

import { announce } from '../../../Announce/AnnounceModel/announce';
import { AnnounceServiceService } from '../../../Announce/AnnounceService/announce-service.service';
import { AuthService } from '../../../auth/auth-service/auth.service';
import { PanelisteService , MyFeedbackItem, PanelistRewards} from '../../../feedback/paneliste.service';
import { RecompensesService,RewardSummary } from '../../../recompenses/recompensesService/recompenses.service';
import { NotificationBellComponent } from '../../../notifications/notification-bell/notification-bell.component';
@Component({
  selector: 'app-home-paneliste',
  standalone: true,
  imports: [CommonModule, RouterModule,NotificationBellComponent],
  templateUrl: './home-paneliste.component.html',
  styleUrl: './home-paneliste.component.css'
})
export class HomePanelisteComponent implements OnInit{

  today = new Date();

  categoryList: Category[] = [];
  announces: announce[] = [];

  /** ce que le paneliste n’a pas encore répondu */
  answerable: announce[] = [];

  /** mes feedbacks + mes rewards (depuis backend) */
  myFbs: MyFeedbackItem[] = [];
 rewards: PanelistRewards = { items: [], totals: {} };
    rewardsById: Record<string, RewardSummary> = {};
  

  constructor(
    private categoryService: CategoryService,
    private announceService: AnnounceServiceService,
    private router: Router,
    public auth: AuthService,
    private psvc: PanelisteService,
    private reward: RecompensesService  
  ) {}

  ngOnInit(): void {
    if (!this.auth.isAuthenticated() || !this.auth.isPaneliste) {
      this.router.navigate(['/login'], { queryParams: { r: '/paneliste/home' } });
      return;
    }
    this.loadCategories();
    this.loadAnnounces();
    this.loadMyFeedbacks();
    this.loadMyRewards();
  }

  /*private recomputeAnswerable() {
    const answered = new Set(this.myFbs.map((x) => x.announcementId));
    this.answerable = (this.announces || []).filter(
      (a) => !answered.has(a.idAnnouncement) && ((a as any).quizList?.length || 0) > 0
    );
  }*/
  private recomputeAnswerable() {
    const answered = new Set<string>(
      (this.myFbs ?? [])
        .map(f => f.announcementId)
        .filter((id): id is string => typeof id === 'string' && id.length > 0)
    );

    this.answerable = (this.announces ?? []).filter((a: any) => {
      const id = a?.idAnnouncement as string | undefined;
      const quizCount = Array.isArray(a?.quizList) ? a.quizList.length : 0;
      return !!id && !answered.has(id) && quizCount > 0;
    });
  }
  loadCategories(): void {
    this.categoryService.getcategorieList().subscribe({
      next: (data) => (this.categoryList = data),
      error: (err) => console.error('Erreur lors du chargement des catégories', err),
    });
  }

  loadAnnounces(): void {
    this.announceService.getAnnounceList().subscribe({
      next: (data) => {
        this.announces = data;
        this.recomputeAnswerable();
        this.loadRewardsFor(this.announces);    // ⬅️ charge les récompenses

      },
      error: (err) => console.error('Erreur chargement annonces :', err),
    });
  }

  loadMyFeedbacks() {
    this.psvc.myFeedbacks().subscribe({
      next: (fb) => {
        this.myFbs = fb ?? [];
        this.recomputeAnswerable();
      },
      error: (e) => console.error('Erreur feedbacks', e),
    });
  }

  loadMyRewards() {
    this.psvc.myRewards().subscribe({
      next: (rw) => (this.rewards = rw ?? { items: [], totals: {} }),
      error: (e) => console.error('Erreur rewards', e),
    });
  }

  getImageUrl(fileName?: string): string {
    return fileName
      ? `http://localhost:8081/Announcement/downloadannounce/${fileName}`
      : 'assets/no-image.png';
  }

  logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }

private loadRewardsFor(list: announce[]) {
  const calls = (list ?? [])
    .filter(a => !!a?.idAnnouncement)
    .map(a =>
      this.reward.getForAnnouncementPublic(a.idAnnouncement!).pipe(
        // arr est bien un recompenses[]
        map(arr => [a.idAnnouncement!, this.reward.summarize(arr)] as const),
        catchError(() => of([a.idAnnouncement!, { text: '', items: [] }] as const))
      )
    );

  if (!calls.length) { this.rewardsById = {}; return; }

  forkJoin(calls).subscribe(entries => {
    this.rewardsById = Object.fromEntries(entries);
  });
}


}






































/*selectedCategory: Category | null = null;
announcesByCategory: announce[] = [];

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
    next: list => this.announcesByCategory = list ?? [],
    error: _ => {
      this.announcesByCategory = this.announces
        .filter(a => this.toNumber((a as any).category?.idcategory) === id);
    }
  });
}

clearCategory(): void {
  this.selectedCategory = null;
  this.announcesByCategory = [];
}



}*/
/*
today = new Date();

  categoryList: Category[] = [];
  announces: announce[] = [];
feedbacks: any[] = [];
rewards: { totals?: any; items?: any[] } = { totals: {}, items: [] };
 constructor(
    private categoryService: CategoryService,
    private announceService: AnnounceServiceService,
        private router: Router,
        public auth: AuthService, private psvc: PanelisteService
  ) {}


  ngOnInit(): void {
    // Sécurité côté UI
    if (!this.auth.isAuthenticated() || !this.auth.isPaneliste) {
      this.router.navigate(['/login'], { queryParams: { r: '/paneliste/home' } });
      return;
    }
    this.loadAll();
    this.loadMyFeedbacks();
    this.loadMyRewards();
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
      next: (data) => (this.announces = data),
      error: (err) => console.error('Erreur chargement annonces :', err),
    });
  }

  getImageUrl(fileName?: string): string {
    return fileName
      ? `http://localhost:8081/Announcement/downloadannounce/${fileName}`
      : 'assets/no-image.png';
  }

  
  
  logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }

  tab: 'all'|'fb'|'rw' = 'all';

  myFbs: any[] = [];
  myRewards: any[] = [];

  

  
  loadAll() { this.psvc.allAnnounces().subscribe(a => this.announces = a ?? []); }
  loadMyFeedbacks() { this.psvc.myFeedbacks().subscribe(fb => this.myFbs = fb ?? []); }
  loadMyRewards() { this.psvc.myRewards().subscribe(rw => this.myRewards = rw ?? []); }

}*/

