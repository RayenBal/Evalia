import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule,Router } from '@angular/router';
import { ParticipationService, QuizAttemptView } from '../participationService/participation.service';
import { AttemptAnswerView } from '../participationService/participation.service';
import { AuthService } from '../../auth/auth-service/auth.service';
@Component({
  selector: 'app-announcement-results',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './announcement-results.component.html',
  styleUrl: './announcement-results.component.css'
})
export class AnnouncementResultsComponent implements OnInit {
  announceId = '';
  attempts: QuizAttemptView[] = [];
  loading = true;
errorMsg = '';
  constructor(
    private route: ActivatedRoute,
    private participation: ParticipationService,
    public auth: AuthService,
        public router: Router
  ) {}

 /* ngOnInit(): void {
    this.announceId = this.route.snapshot.paramMap.get('id') || '';
    this.participation.getAttemptsForAnnounce(this.announceId).subscribe({
      next: data => { this.attempts = data; this.loading = false; },
      error: _ => { this.loading = false; }
    });
  }*/
   /*ngOnInit(): void {
    this.announceId = this.route.snapshot.paramMap.get('id') || '';
    this.participation.getAttemptsForAnnounce(this.announceId).subscribe({
      next: data => { this.attempts = data; this.loading = false; },
      error: (e) => {
        this.loading = false;
        this.errorMsg = e?.status === 403
          ? 'Accès refusé : vous devez être l’annonceur propriétaire pour voir ces réponses.'
          : 'Erreur lors du chargement des réponses.';
      }
    });
  }*/
ngOnInit(): void {
  this.announceId = this.route.snapshot.paramMap.get('id') || '';
  const quizId = this.route.snapshot.queryParamMap.get('quiz'); // <-- récupère ?quiz=...

  const obs = quizId
    ? this.participation.getAttemptsForAnnounceAndQuiz(this.announceId, quizId)
    : this.participation.getAttemptsForAnnounce(this.announceId);

  obs.subscribe({
    next: data => { this.attempts = data; this.loading = false; },
    error: e => {
      this.loading = false;
      this.errorMsg = e?.status === 403
        ? 'Accès refusé.'
        : 'Erreur lors du chargement des réponses.';
    }
  });
}
  joinSelected(ans: AttemptAnswerView): string {
    const s: any = (ans as any)?.selected;
    if (Array.isArray(s)) {
      return s.join(', ');
    }
    return s ?? '';
  }
      logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }
}