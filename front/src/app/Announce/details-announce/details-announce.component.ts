import { Component, OnInit,PLATFORM_ID,Inject } from '@angular/core';
import { ActivatedRoute,Router,RouterModule } from '@angular/router';
import { CommonModule ,isPlatformBrowser } from '@angular/common';
import { AnnounceServiceService } from '../AnnounceService/announce-service.service';
import { announce } from '../AnnounceModel/announce';
import { QuizService } from '../../quiz/quizService/quiz.service';
import { TypeRecompenses,RecompenseNew } from '../../recompenses/recompensesModel/recompenses';
import { RecompensesService } from '../../recompenses/recompensesService/recompenses.service';
import { AuthService } from '../../auth/auth-service/auth.service';
@Component({
  selector: 'app-details-announce',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './details-announce.component.html',
  styleUrl: './details-announce.component.css'
})
export class DetailsAnnounceComponent implements OnInit{
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
    public auth: AuthService ,
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
/* private loadPanelistId(): void {
 
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
  }*/
 private loadPanelistId(): void {
  if (!isPlatformBrowser(this.platformId)) {          // 👈 garde SSR/nœud
    this.panelistId = null;
    return;
  }
  try {
    const raw = localStorage.getItem('auth_user');    // 👈 OK uniquement navigateur
    if (raw) {
      const user = JSON.parse(raw);
      this.panelistId = Number(user?.id_user ?? user?.id ?? null);
    } else {
      this.panelistId = null;
    }
  } catch {
    this.panelistId = null;
  }
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


  deleteQuiz(quizId: string): void {
    if (!quizId) return;
    if (!confirm('Voulez-vous vraiment supprimer ce quiz ?')) return;

    this.quizService.deleteQuiz(quizId).subscribe({
      next: () => {
        alert('Quiz supprimé avec succès ✅');

        // ⬇️ protège contre announce/quizList null/undefined
        if (this.announce?.quizList) {
          this.announce.quizList = this.announce.quizList.filter(
            (q: any) => this.getQuizId(q) !== quizId
          );
        } else {
          // au cas où, on recharge depuis le backend
          this.refreshAnnounce();
        }
      },
      error: (err) => {
        console.error('Erreur lors de la suppression', err);
        alert('❌ Erreur lors de la suppression du quiz');
      }
    });
  }
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

  deleteRecompense(id: string): void {
    if (!id) return;
    if (!confirm('Supprimer cette récompense ?')) return;

    this.recompensesService.deleteRecompenses(id).subscribe({
      next: () => {
        alert('Récompense supprimée ✅');
        if (this.announce?.recompensesList) {
          this.announce.recompensesList = this.announce.recompensesList
            .filter((r: any) => this.getRecId(r) !== id);
        } else {
          this.refreshAnnounce();
        }
      },
      error: (err) => {
        console.error('Erreur suppression récompense', err);
        alert('❌ Erreur lors de la suppression');
      }
    });
  }

  logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }
  
}


