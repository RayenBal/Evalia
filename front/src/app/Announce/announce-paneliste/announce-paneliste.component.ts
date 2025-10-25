
import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { ActivatedRoute,Router,RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AnnounceServiceService } from '../AnnounceService/announce-service.service';
import { announce } from '../AnnounceModel/announce';
import { QuizService } from '../../quiz/quizService/quiz.service';
import { RecompensesService } from '../../recompenses/recompensesService/recompenses.service';
import { AuthService } from '../../auth/auth-service/auth.service';
import { FeedbackFormComponent } from '../../feedback/feedback-form/feedback-form.component';
@Component({
  selector: 'app-announce-paneliste',
  standalone: true,
  imports: [CommonModule, RouterModule,FeedbackFormComponent],
  templateUrl: './announce-paneliste.component.html',
  styleUrl: './announce-paneliste.component.css'
})
export class AnnouncePanelisteComponent implements OnInit{

 baseUrl = 'http://localhost:8081/Announcement';
   announceId: string = '';
   



  // 👇 ajouté : id du paneliste connecté
  panelistId: number | null = null;




  //announce!: announce;
announce: announce | null = null;
  
  constructor(
    private route: ActivatedRoute,
    private announceService: AnnounceServiceService,
    private quizService: QuizService,
    private recompensesService: RecompensesService ,
    public auth: AuthService,
    private router:Router,
    @Inject(PLATFORM_ID) private platformId: Object
  ) {}

  ngOnInit(): void {
    this.announceId = this.route.snapshot.paramMap.get('id') || '';
     this.loadPanelistId();
    this.announceService.getAnnounce(this.announceId).subscribe(data => {
      this.announce = data;
      this.refreshAnnounce();
    });
  }
 private loadPanelistId(): void {
    // Vérifier si nous sommes dans le navigateur (protection SSR)
    if (!isPlatformBrowser(this.platformId)) {
      this.panelistId = null;
      return;
    }

    // Exemples possibles suivant ce que tu stockes :
    // 1) si tu as un objet user en localStorage
    const raw = localStorage.getItem('auth_user');
    if (raw) {
      try {
        const user = JSON.parse(raw);
        this.panelistId = Number(user?.id_user ?? user?.id ?? null);
      } catch {
        this.panelistId = null;
      }
    }

    // 2) OU remplace par ton AuthService:
    // this.panelistId = this.auth.currentUser()?.id_user ?? null;
  }
  
refreshAnnounce(): void {
    this.announceService.getAnnounce(this.announceId).subscribe({
      next: (data) => {
        // normalise quizList pour éviter undefined
        (data as any).quizList = (data as any).quizList ?? [];
        this.announce = data;
      },
      error: (err) => {
        console.error('❌ Erreur lors du chargement de l’annonce', err);
      }
    });
  }
  getImageUrl(fileName: string | undefined): string {
    return fileName ? `http://localhost:8081/Announcement/downloadannounce/${fileName}` : '';
  }
  formatTestMode(mode: string): string {
  switch (mode) {
    case 'HOME_DELIVERY':
      return 'Livraison à domicile';
    case 'OFFICE_TESTING':
      return 'Test en bureau';
    case 'REMOTE_TESTING':
      return 'Test à distance';
    default:
      return mode;
  }
}
  getQuizId(q: any): string {
    // Essaie dans l'ordre ce que tu pourrais avoir côté DTO
    return q?.idQuiz ?? q?.id ?? q?.uuid ?? '';
  }

  trackByQuiz = (_: number, q: any) => this.getQuizId(q);


  
  
formatRewardType(t?: string): string {
  switch (t) {
    case 'Points': return 'Points';
    case 'Argent': return 'Argent';
    case 'BonsDachats': return 'Bons d’achats';
    default: return t ?? '';
  }
}

  // ----- Récompenses -----
  getRecId(r: any): string {
    // selon ta sérialisation: idRecompense (JSON) ou autre
    return r?.idRecompense ?? r?.id ?? r?.uuid ?? '';
  }
  trackByRec = (_: number, r: any) => this.getRecId(r);

    logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }
  }
