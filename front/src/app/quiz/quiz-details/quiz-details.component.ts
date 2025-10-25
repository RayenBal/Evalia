import { Component, OnInit } from '@angular/core';

import { ActivatedRoute, Router } from '@angular/router';
import { ResponsePaneliste } from '../../responsePaneliste/ResponsePanelisteModel/responsepaneliste';
import { quiz } from '../quizModel/quiz';
import { QuizService } from '../quizService/quiz.service';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../auth/auth-service/auth.service';
import { question } from '../../question/questionModel/question';
import { AnnounceServiceService } from '../../Announce/AnnounceService/announce-service.service';
import { announce } from '../../Announce/AnnounceModel/announce';
@Component({
  selector: 'app-quiz-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quiz-details.component.html',
  styleUrl: './quiz-details.component.css'
})
export class QuizDetailsComponent implements OnInit {
  get isAnnonceur() { return this.auth.userType === 'Announceur'; }
  get isPaneliste() { return this.auth.userType === 'Paneliste'; }
  quizId: string = '';
  quiz: quiz | null = null;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private quizService: QuizService,public auth:AuthService,private announceSvc: AnnounceServiceService
  ) {}

  /*ngOnInit(): void {
    this.quizId = this.route.snapshot.paramMap.get('id') ?? '';
    if (this.quizId) {
      this.quizService.getQuiz(this.quizId).subscribe({
        next: (data) => this.quiz = data,
        error: () => {
          alert("Erreur lors du chargement du quiz");
          this.router.navigate(['/quiz/list']);
        }
      });
    }
  }*/
 setSelected(a: announce) {
  this.selectedId = a.idAnnouncement;
}
selectedId?: string;
 ngOnInit(): void {
 
  this.quizId = this.route.snapshot.paramMap.get('id') ?? '';
  if (!this.quizId) return;

  this.quizService.getQuiz(this.quizId).subscribe({
    next: (data) => {
      data.questions = data.questions ?? [];
      data.questions.forEach(q => {
        q.responses = (q.responses ?? []).map((r: any) => ({
          idResponsePaneliste: r.idResponsePaneliste ?? r.IdResponsePaneliste ?? r.id ?? '',
          content: r.content ?? '',
        })) as ResponsePaneliste[];
      });
      this.quiz = data;
    },
    error: () => {
      alert('Erreur lors du chargement du quiz');
      this.router.navigate(['/quiz/list']);
    }
  });
  
}

   logout() {
    this.auth.clearToken();
    this.router.navigate(['/login']);
  }
    goBack() {
    this.router.navigate(['/annonceur/home']);
  }
}
