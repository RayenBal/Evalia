import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { QuizService } from '../quizService/quiz.service';
import { AuthService } from '../../auth/auth-service/auth.service';
@Component({
  selector: 'app-edit-quiz',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './edit-quiz.component.html',
  styleUrl: './edit-quiz.component.css'
})
export class EditQuizComponent {

quizId!: string;
  form!: FormGroup;
  loading = false;
    announceId: string | null = null;   

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private quizService: QuizService,
    public auth: AuthService
  ) {}
selectedId?: string;
  ngOnInit(): void {
    this.quizId = this.route.snapshot.paramMap.get('id') || '';
    this.announceId = this.route.snapshot.queryParamMap.get('announceId'); // ← NEW
    this.form = this.fb.group({
      content: ['', Validators.required],
      questions: this.fb.array([]),
    });
    this.loadQuiz();
  }

  get questions(): FormArray { return this.form.get('questions') as FormArray; }
  responsesOf(i: number): FormArray {
    return (this.questions.at(i) as FormGroup).get('responses') as FormArray;
  }

  loadQuiz(): void {
    this.loading = true;
    this.quizService.getQuiz(this.quizId).subscribe({
      next: (q: any) => {

            if (!this.announceId) {
          this.announceId =
            q?.announcement?.idAnnouncement ??
            q?.announce?.idAnnouncement ??
            q?.announcementId ??
            null;
        }



        this.form.patchValue({ content: q?.content || '' });
        (q?.questions || []).forEach((qq: any) => this.addQuestion(qq));
        this.loading = false;
      },
      error: () => { this.loading = false; alert('❌ Erreur de chargement'); }
    });
  }

  addQuestion(q?: any): void {
    const group = this.fb.group({
      idQuestion: [q?.idQuestion || null],
      content: [q?.content || '', Validators.required],
      responses: this.fb.array([]),
    });
    (q?.responses || []).forEach((r: any) => {
      (group.get('responses') as FormArray).push(
        this.fb.group({
          idResponsePaneliste: [r?.idResponsePaneliste || null],
          content: [r?.content || '', Validators.required],
        })
      );
    });
    if (!q?.responses || q.responses.length === 0) {
      (group.get('responses') as FormArray).push(this.fb.group({
        idResponsePaneliste: [null], content: ['', Validators.required]
      }));
      (group.get('responses') as FormArray).push(this.fb.group({
        idResponsePaneliste: [null], content: ['', Validators.required]
      }));
    }
    this.questions.push(group);
  }

  removeQuestion(i: number): void { this.questions.removeAt(i); }

  addResponse(qIndex: number, r?: any): void {
    this.responsesOf(qIndex).push(this.fb.group({
      idResponsePaneliste: [r?.idResponsePaneliste || null],
      content: [r?.content || '', Validators.required],
    }));
  }
  removeResponse(qIndex: number, rIndex: number): void { this.responsesOf(qIndex).removeAt(rIndex); }

  submit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); alert('Champs requis'); return; }
    const payload = {
      content: this.form.value.content,
      questions: (this.form.value.questions || []).map((q: any) => ({
        idQuestion: q.idQuestion || null,
        content: q.content,
        responses: (q.responses || []).map((r: any) => ({
          idResponsePaneliste: r.idResponsePaneliste || null,
          content: r.content,
        })),
      })),
    };
    this.quizService.updateQuiz(this.quizId, payload as any).subscribe({
      next: (updated:any) => {
        // Dernière chance : récupérer l'id depuis la réponse si absent
        const id =
          this.announceId ??
          updated?.announcement?.idAnnouncement ??
          updated?.announce?.idAnnouncement ??
          updated?.announcementId ??
          null;

        if (id) {
          this.router.navigate(['/announcement/details', id]);
        } else {
          // Fallback pour éviter NG04008
          console.warn('announceId manquant, retour à la liste');
          this.router.navigate(['/announces']);
        }
      },
      error: () => { alert('❌ Erreur lors de la mise à jour'); }
    });
  }

  cancel(): void { this.router.navigate(['/announcement/details', this.announceId]); }
    logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }

}
