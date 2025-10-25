
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule} from '@angular/router';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule,FormControl,Validators } from '@angular/forms';
import { AnnounceServiceService } from '../../Announce/AnnounceService/announce-service.service';
import { ParticipationService } from '../participationService/participation.service';
import { QuizService } from '../../quiz/quizService/quiz.service';
import { AuthService } from '../../auth/auth-service/auth.service';
@Component({
  selector: 'app-take-quiz',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './take-quiz.component.html',
  styleUrl: './take-quiz.component.css'
})
export class TakeQuizComponent  implements OnInit {
  /*announceId!: string;
  quizId!: string;
  panelistId!: number;

  announce: any;
  quiz: any;

  form!: FormGroup;
  attemptId?: string;
  submitting = false;
  loading = true;
  error?: string;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private announces: AnnounceServiceService,
    private participation: ParticipationService
  ) {}

  ngOnInit(): void {
    this.announceId = this.route.snapshot.paramMap.get('announceId') || '';
    this.quizId = this.route.snapshot.paramMap.get('quizId') || '';
    // panelistId depuis query param (ex: ?panelistId=42) ou fixe si tu l’as via auth
    const qpPid = this.route.snapshot.queryParamMap.get('panelistId');
    this.panelistId = qpPid ? Number(qpPid) : 1; // fallback (à adapter)

    // 1) Charger l’annonce (avec quiz/questions/réponses)
    this.announces.getAnnounce(this.announceId).subscribe({
      next: (a) => {
        this.announce = a;
        this.quiz = (a.quizList || []).find((q: any) =>
          (q.idQuiz || q.id || q.uuid) === this.quizId
        ) || (a.quizList || [])[0]; // fallback si besoin
        if (!this.quiz) {
          this.error = 'Quiz introuvable dans cette annonce.';
          this.loading = false;
          return;
        }
        // 2) Construire le form
        this.buildForm();
        // 3) Démarrer la tentative
        this.participation.startAttempt(this.announceId, this.quizId, this.panelistId).subscribe({
          next: att => { this.attemptId = att.idAttempt; this.loading = false; },
          error: e => { this.error = 'Impossible de démarrer la tentative.'; this.loading = false; console.error(e); }
        });
      },
      error: (e) => { this.error = 'Erreur chargement annonce.'; this.loading = false; console.error(e); }
    });
  }

  private buildForm(): void {
    this.form = this.fb.group({
      answers: this.fb.array(
        (this.quiz.questions || []).map((q: any) =>
          this.fb.group({
            questionId: [q.idQuestion || q.id || q.uuid],
            selectedResponseId: [null],  // radio
            freeText: ['']               // optionnel
          })
        )
      )
    });
  }

  get answersFA(): FormArray {
    return this.form.get('answers') as FormArray;
  }

  onSubmit(): void {
    if (!this.attemptId) return;
    this.submitting = true;

    const answersPayload = this.answersFA.value
      .filter((row: any) => row.selectedResponseId) // n’envoie que si un choix a été coché
      .map((row: any) => ({
        question: { idQuestion: row.questionId },
        selectedResponse: { idResponsePaneliste: row.selectedResponseId },
        freeText: row.freeText || null
      }));

    this.participation.submitRaw(this.attemptId, answersPayload).subscribe({
      next: () => {
        this.submitting = false;
        alert('Réponses soumises. Merci !');
        this.router.navigate(['/announcement/details', this.announceId]);
      },
      error: (e) => {
        console.error(e);
        this.submitting = false;
        alert('Erreur lors de la soumission.');
      }
    });
  }*/

  /*  announceId = '';
  quizId = '';
  panelistId = 0; // récupère le vrai id via ton AuthService
  quiz: any;
  form!: FormGroup;
  attemptId?: string;
  submitting = false;
  done = false;

  get qControls() { return (this.form.get('questions') as FormArray).controls; }

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private participation: ParticipationService,
    private quizService: QuizService,
     private auth: AuthService
  ) {}

  ngOnInit(): void {
    
    this.announceId = this.route.snapshot.paramMap.get('announceId') || '';
    this.quizId = this.route.snapshot.paramMap.get('quizId') || '';

    // ⚠️ remplace par l’id du user connecté
   // this.panelistId = Number(this.route.snapshot.queryParamMap.get('panelistId') ?? 0);
// ✅ garde: il faut être connecté et paneliste
    if (!this.auth.isAuthenticated || !this.auth.isPaneliste) {
      // soit tu rediriges:
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url }});
      return;

      // ou tu pourrais afficher un message et désactiver le bouton submit
    }

    // ✅ id du paneliste depuis le token (fallback sur query param si besoin)
    this.panelistId =
      this.auth.userId  ?? 0;
    //  Number(this.route.snapshot.queryParamMap.get('panelistId') ?? 0);
    // Charger le quiz et construire le form
    this.quizService.getQuiz(this.quizId).subscribe(q => {
      this.quiz = q;
      this.buildForm();
      // Démarrer la tentative
      this.participation.startAttempt(this.announceId, this.quizId)
        .subscribe(attempt => this.attemptId = attempt.idAttempt);
    });
  }

  buildForm(): void {
    this.form = this.fb.group({
      questions: this.fb.array(
        (this.quiz?.questions|| []).map(() =>
          this.fb.group({
            selectedResponseId: [null],
            freeText: ['']
          })
        )
      )
    });
  }

  submit(): void {
    if (!this.attemptId) return;
    this.submitting = true;

    const answers = this.qControls.map((ctrl, i) => ({
      question: { idQuestion: this.quiz.questions[i].idQuestion },
      selectedResponse: { IdResponsePaneliste: ctrl.value.selectedResponseId },
      freeText: ctrl.value.freeText || null
    }));

    this.participation.submitRaw(this.attemptId, answers).subscribe({
      next: () => { this.submitting = false; this.done = true; },
      error: (e) => { console.error(e); this.submitting = false; }
    });
  }
}*/
/* announceId = '';
  quizId = '';
  panelistId = 0;
  quiz: any;

  form!: FormGroup;
  attemptId?: string;
  submitting = false;
  done = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private participation: ParticipationService,
    private quizService: QuizService,
    private auth: AuthService
  ) {}

  // ------- helpers typés (évite AbstractControl|null) -------
  private get questionsFA(): FormArray<FormGroup> {
    return this.form.get('questions') as FormArray<FormGroup>;
  }
  getSelected(i: number): FormControl {
    return this.questionsFA.at(i).get('selectedResponseId') as FormControl;
  }
 getSelected(i: number): FormControl {
    return (this.form.get('questions') as FormArray)
      .at(i)
      .get('selectedResponseId') as FormControl;
  }

  getFreeText(i: number): FormControl {
    return this.questionsFA.at(i).get('freeText') as FormControl;
  }
  // ----------------------------------------------------------

  ngOnInit(): void {
    this.announceId = this.route.snapshot.paramMap.get('announceId') || '';
    this.quizId     = this.route.snapshot.paramMap.get('quizId') || '';

    // ⚠️ il faut appeler la méthode !
    if (!this.auth.isAuthenticated || !this.auth.isPaneliste) {  // <-- () ajouté
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url }});
      return;
    }

    this.panelistId = this.auth.userId ?? 0;

    this.quizService.getQuiz(this.quizId).subscribe(q => {
      this.quiz = q;
      this.buildForm();

      // Démarrer la tentative (JWT ajouté par l’intercepteur)
      this.participation
        .startAttempt(this.announceId, this.quizId)
        .subscribe(attempt => (this.attemptId = attempt.idAttempt));
    });
  }

  buildForm(): void {
    this.form = this.fb.group({
      questions: this.fb.array(
        (this.quiz?.questions || []).map(() =>
          this.fb.group({
            selectedResponseId: [null], // <-- required (optionnel)
            freeText: ['']
          })
        )
      )
    });
  }

  submit(): void {
    if (!this.attemptId) return;
    if (this.form.invalid) return; // si Validators.required gardé

    const answers = this.questionsFA.controls.map((grp, i) => ({
      question: { idQuestion: this.quiz.questions[i].idQuestion },
      // ⚠️ clé correcte attendue par le backend (camelCase)
      selectedResponse: {
        idResponsePaneliste: (grp.get('selectedResponseId') as FormControl).value // <-- corrigé
      },
      freeText: (grp.get('freeText') as FormControl).value || null
    }));

    this.submitting = true;
    this.participation.submitRaw(this.attemptId, answers).subscribe({
      next: () => { this.submitting = false; this.done = true; },
      error: (e) => { console.error(e); this.submitting = false; }
    });
  }*/
   announceId = '';
  quizId = '';
  panelistId = 0;
  quiz: any;

  form!: FormGroup;
  attemptId?: string;
  submitting = false;
  done = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private fb: FormBuilder,
    private participation: ParticipationService,
    private quizService: QuizService,
    public auth: AuthService
  ) {}

  ngOnInit(): void {
    this.announceId = this.route.snapshot.paramMap.get('announceId') || '';
    this.quizId = this.route.snapshot.paramMap.get('quizId') || '';

    if (!this.auth.isAuthenticated || !this.auth.isPaneliste) {
      this.router.navigate(['/login'], { queryParams: { returnUrl: this.router.url }});
      return;
    }

    this.panelistId = this.auth.userId ?? 0;

    this.quizService.getQuiz(this.quizId).subscribe(q => {
      this.quiz = q;
      this.buildForm();
      this.participation.startAttempt(this.announceId, this.quizId)
        .subscribe(attempt => this.attemptId = attempt.idAttempt);
    });
  }

  // ---------- Form ----------
  buildForm(): void {
    this.form = this.fb.group({
      questions: this.fb.array(
        (this.quiz?.questions || []).map((q: any) =>
          this.fb.group({
            // un bool par réponse pour permettre multi-sélection
            choices: this.fb.array(q.responses.map(() => this.fb.control(false))),
            freeText: ['']
          })
        )
      )
    });
  }

  get questionsFA(): FormArray {
    return this.form.get('questions') as FormArray;
  }
  getChoicesFA(i: number): FormArray {
    return this.questionsFA.at(i).get('choices') as FormArray;
  }
  getChoice(i: number, k: number): FormControl {
    return this.getChoicesFA(i).at(k) as FormControl;
  }

  // ---------- Submit ----------
  submit(): void {
    if (!this.attemptId) return;
    this.submitting = true;

    const payload: any[] = [];

    this.quiz.questions.forEach((q: any, i: number) => {
      const choices = this.getChoicesFA(i).value as boolean[];
      const freeText = this.questionsFA.at(i).get('freeText')?.value || null;

      choices.forEach((checked, k) => {
        if (checked) {
          const resp = q.responses[k];
          payload.push({
            question: { idQuestion: q.idQuestion },
            selectedResponse: {
              idResponsePaneliste:
                resp.idResponsePaneliste ?? resp.IdResponsePaneliste ?? resp.id
            },
            freeText
          });
        }
      });
    });

    this.participation.submitRaw(this.attemptId!, payload).subscribe({
      next: () => { this.submitting = false; this.done = true; },
      error: (e) => { console.error(e); this.submitting = false; }
    });
  }

  
  logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }
}


