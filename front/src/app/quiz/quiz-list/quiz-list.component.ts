import { Component } from '@angular/core';
import { QuizService } from '../quizService/quiz.service';
import { Router,RouterModule } from '@angular/router';
import { quiz } from '../quizModel/quiz';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-quiz-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './quiz-list.component.html',
  styleUrl: './quiz-list.component.css'
})
export class QuizListComponent {

quizList: quiz[] = [];

  constructor(private quizService: QuizService, public router: Router) {}

  ngOnInit(): void {
    this.loadQuizzes();
  }

  loadQuizzes(): void {
    this.quizService.getQuizList().subscribe({
      next: (data) => {
        this.quizList = data;
      },
      error: (err) => {
        console.error('Erreur lors du chargement des quiz', err);
      }
    });
  }

  deleteQuiz(id: string): void {
    if (confirm("Êtes-vous sûr de vouloir supprimer ce quiz ?")) {
      this.quizService.deleteQuiz(id).subscribe({
        next: () => {
          this.quizList = this.quizList.filter(q => q.idQuiz !== id);
        },
        error: (err) => {
          console.error('Erreur lors de la suppression du quiz', err);
        }
      });
    }
  }
}
