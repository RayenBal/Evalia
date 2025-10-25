import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AnnounceServiceService } from '../../Announce/AnnounceService/announce-service.service';
import { announce } from '../../Announce/AnnounceModel/announce';
import { AuthService } from '../../auth/auth-service/auth.service';
import { log } from 'console';
import { FeedbackStatsDto } from '../../feedback/feedback-owner/feedback-owner.service';
import { FeedbackOwnerService } from '../../feedback/feedback-owner/feedback-owner.service';
import { forkJoin, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { RecompenseNew ,TypeRecompenses} from '../../recompenses/recompensesModel/recompenses';
import { RecompensesService,RewardSummary  } from '../../recompenses/recompensesService/recompenses.service';
import { CategoryService } from '../../category/categoryService/category.service';
import { Category } from '../../category/categoryModel/category';
import { NotificationBellComponent } from '../../notifications/notification-bell/notification-bell.component';

@Component({
  selector: 'app-home-annonceur',
  standalone: true,
  imports: [CommonModule, RouterModule,NotificationBellComponent],
  templateUrl: './home-annonceur.component.html',
  styleUrl: './home-annonceur.component.css'
})
export class HomeAnnonceurComponent implements OnInit {


  today = new Date();
    categoryList: Category[] = [];
  loading = true;
  error?: string;
  announces: announce[] = [];
statsById: Record<string, FeedbackStatsDto> = {};
  rewardsById: Record<string, RewardSummary> = {};

  constructor(
    private svc: AnnounceServiceService,
    private router: Router,
    public auth: AuthService,
    private fbOwner: FeedbackOwnerService,
    private rewards: RecompensesService  ,
        private categoryService: CategoryService,
     
  ) {}

  ngOnInit(): void {
    this.loadAnnounces(); // ta méthode existante
    this.loadCategories();
  }

   // Quand tes annonces sont chargées, appelle ceci :
  private loadStatsFor(list: announce[]) {
    const calls = (list ?? [])
      .filter(a => !!a?.idAnnouncement)
      .map(a =>
        this.fbOwner.stats(a.idAnnouncement!).pipe(
          map(s => [a.idAnnouncement!, s] as const),
          catchError(() => of([a.idAnnouncement!, { total: 0, average: 0 }] as const))
        )
      );

    if (!calls.length) { this.statsById = {}; return; }

    forkJoin(calls).subscribe(entries => {
      this.statsById = Object.fromEntries(entries);
    });
  }
selectedId?: string;
  // ➜ Appelle loadStatsFor() juste après avoir mis this.announces
  private loadAnnounces() {
    this.loading = true;
    this.svc.getMyAnnounces(/* adapte si besoin */).subscribe({
      next: list => {
        this.announces = list ?? [];
              this.selectedId = this.announces[0]?.idAnnouncement ?? undefined; // par défaut

        this.loading = false;
        this.loadStatsFor(this.announces);
        this.loadRewardsFor(this.announces);    // ⬅️ charge les récompenses
      },
      error: err => { this.error = err?.message || 'Erreur'; this.loading = false; }
    });
  }
setSelected(a: announce) {
  this.selectedId = a.idAnnouncement;
}
  // util pour les étoiles
  star(n: number | null | undefined): string {
    const v = Math.max(0, Math.min(5, Math.round((n ?? 0) * 2) / 2)); // arrondi 0.5
    const full = Math.floor(v);
    const half = v - full >= 0.5 ? '⯪' : '';
    return '★'.repeat(full) + half + '☆'.repeat(5 - full - (half ? 1 : 0));
  }
 loadCategories(): void {
    this.categoryService.getcategorieList().subscribe({
      next: (data) => (this.categoryList = data),
      error: (err) => console.error('Erreur lors du chargement des catégories', err),
    });
  }

  /*ngOnInit(): void {
    this.svc.getMyAnnounces().subscribe({
      next: a => { this.announces = a ?? []; this.loading = false; },
      error: e => { this.error = 'Impossible de charger vos annonces.'; this.loading = false; }
    });
  }*/

  img(f?: string) {
    return f ? `http://localhost:8081/Announcement/downloadannounce/${f}` : 'assets/no-image.png';
  }

  delete(a: announce) {
    if (!a.idAnnouncement) return;
    if (!confirm('Supprimer cette annonce ?')) return;
    this.svc.deleteAnnounce(a.idAnnouncement).subscribe({
      next: () => this.announces = this.announces.filter(x => x.idAnnouncement !== a.idAnnouncement)
    });
  }

  logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
    this.auth.getToken();
    console.log(this.auth.getToken());
    
  }

  get userLabel(): string {
    // petit plus : affiche l’email si dispo
    return this.auth.email ?? 'Compte annonceur';
  }



    private loadRewardsFor(list: announce[]) {
    const calls = (list ?? [])
      .filter(a => !!a?.idAnnouncement)
      .map(a =>
        this.rewards.getForAnnouncement(a.idAnnouncement!).pipe(
          map(arr => [a.idAnnouncement!, this.rewards.summarize(arr)] as const),
          catchError(() => of([a.idAnnouncement!, { text: '', items: [] }] as const))
        )
      );

    if (!calls.length) { this.rewardsById = {}; return; }

    forkJoin(calls).subscribe(entries => {
      this.rewardsById = Object.fromEntries(entries);
    });
  }}