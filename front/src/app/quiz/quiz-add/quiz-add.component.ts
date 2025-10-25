import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule,FormArray } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { QuizService } from '../quizService/quiz.service';
import { AuthService } from '../../auth/auth-service/auth.service';

@Component({
  selector: 'app-quiz-add',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './quiz-add.component.html',
  styleUrl: './quiz-add.component.css'
})
export class QuizAddComponent implements OnInit{
   quizForm!: FormGroup;
  isEdit = false;
 quizId: string = '';
 announceId = '';

  constructor(
    private fb: FormBuilder,
    private quizService: QuizService,
    private route: ActivatedRoute,
    private router: Router,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.quizId = this.route.snapshot.paramMap.get('id') || '';
    this.isEdit = !!this.quizId;
    this.announceId = this.route.snapshot.queryParamMap.get('announceId') || '';
    this.initForm();

    if (this.isEdit) {
      this.quizService.getQuiz(this.quizId).subscribe(data => {
        this.populateForm(data);
      });
    }
  }

  initForm() {
    this.quizForm = this.fb.group({
      content: ['', Validators.required],
      questions: this.fb.array([this.createQuestionGroup()])
    });
  }

  createQuestionGroup(): FormGroup {
    return this.fb.group({
      content: ['', Validators.required],
      responses: this.fb.array([this.createResponseGroup()])
    });
  }

  createResponseGroup(): FormGroup {
    return this.fb.group({
      content: ['', Validators.required],
      isCorrect: [false]
    });
  }

  get questions(): FormArray {
    return this.quizForm.get('questions') as FormArray;
  }

  getResponses(qIndex: number): FormArray {
    return this.questions.at(qIndex).get('responses') as FormArray;
  }

  addQuestion(): void {
    this.questions.push(this.createQuestionGroup());
  }

  removeQuestion(index: number): void {
    this.questions.removeAt(index);
  }

  addResponse(qIndex: number): void {
    this.getResponses(qIndex).push(this.createResponseGroup());
  }

  removeResponse(qIndex: number, rIndex: number): void {
    this.getResponses(qIndex).removeAt(rIndex);
  }

  populateForm(quiz: any): void {
    this.quizForm.patchValue({ content: quiz.content });
    const questionArray = this.quizForm.get('questions') as FormArray;
    questionArray.clear();

    quiz.questions.forEach((q: any) => {
      const questionGroup = this.createQuestionGroup();
      questionGroup.patchValue({ content: q.content });

      const responseArray = questionGroup.get('responses') as FormArray;
      responseArray.clear();

      q.responses.forEach((r: any) => {
        const responseGroup = this.createResponseGroup();
        responseGroup.patchValue(r);
        responseArray.push(responseGroup);
      });

      questionArray.push(questionGroup);
    });
  }

  /*onSubmit(): void {
    const quizData = this.quizForm.value;

    if (this.isEdit) {
      this.quizService.updateQuiz(this.quizId, quizData).subscribe(() => {
        alert('Quiz modifié avec succès');
        this.router.navigate(['/quiz']);
      });
    } else {
      this.quizService.createQuiz(quizData).subscribe(() => {
        alert('Quiz ajouté avec succès');
        this.router.navigate(['/quiz']);
      });
    }
  }*/


    onSubmit(): void {
    const quizData = {
      ...this.quizForm.value,
      announceId: this.announceId || null   // <-- association à l’annonce
    };

    if (this.isEdit) {
      this.quizService.updateQuiz(this.quizId, quizData).subscribe(() => {
        alert('Quiz modifié avec succès');
        // si announceId existe, retour vers les détails de l’annonce
        if (this.announceId) {
          this.router.navigate(['/announcement/details', this.announceId]);
        } else {
          this.router.navigate(['/quiz']);
        }
      });
    } else {
      this.quizService.createQuiz(quizData, this.announceId).subscribe(() => {
        alert('Quiz ajouté avec succès');
        if (this.announceId) {
          this.router.navigate(['/announcement/details', this.announceId]);
        } else {
          this.router.navigate(['/quiz']);
        }
      });
    }
  }

  logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }

}
