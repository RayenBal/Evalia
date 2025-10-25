import { Component, OnInit, Inject, PLATFORM_ID } from '@angular/core';
import { FormGroup, FormBuilder, Validators, FormArray, ReactiveFormsModule, FormControl } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule, isPlatformBrowser } from '@angular/common';
import { AnnounceServiceService } from '../AnnounceService/announce-service.service';
import { announce } from '../AnnounceModel/announce';
import { HttpClient } from '@angular/common/http';
import { Category } from '../../category/categoryModel/category';
import { TypeRecompenses,RecompenseNew } from '../../recompenses/recompensesModel/recompenses';
import { AuthService } from '../../auth/auth-service/auth.service';

@Component({
  selector: 'app-add-announce',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './add-announce.component.html',
  styleUrl: './add-announce.component.css'
})
export class AddAnnounceComponent implements OnInit{
  

private baseUrl = 'http://localhost:8081/Announcement';
  
 /* announcementForm = this.fb.group({
    announceName: ['', Validators.required],
    content: ['', Validators.required],
   testModes: [[]], // ou this.fb.array([])
    deliveryAddress: [''],
    category: this.fb.group({ idcategory: [null, Validators.required] }),
    officeAddress: [''],
    quizList: this.fb.array([]),
    recompensesList: this.fb.array([this.createRewardRow()]),
  });*/





announcementForm!: FormGroup;
  mainImageFile?: File;
  productImageFiles: File[] = [];
  categories: Category[] = [];
  //typeRecompensesEnum = TypeRecompenses;
  typeRecompenseOptions = Object.values(TypeRecompenses);
  constructor(public auth: AuthService,private fb: FormBuilder, private http: HttpClient, private router: Router,private announceService:AnnounceServiceService
  ) {}

  ngOnInit(): void {
     if (!this.auth.isAuthenticated() || this.auth.userType !== 'Announceur') {
      // évite toute boucle: redirige vers une AUTRE page
      this.router.navigate(['/login'], { queryParams: { r: '/addAnnounce' } });
      return;
    }
    this.announcementForm = this.fb.group({
      announceName: ['', Validators.required],
      content: ['', Validators.required],
      testModes: [[]], // ou this.fb.array([]) si tu veux les checkboxes
     deliveryAddress: [''],
     //idcategory: [null, Validators.required],
    category: this.fb.group({ idcategory: [null, Validators.required] }),
     officeAddress: [''],
       quizList: this.fb.array([]),
          recompensesList: this.fb.array([
        this.createRewardRow()
      ]),
     // quizList: this.fb.array([this.createQuizGroup()])
    });
     // Charger les catégories
    console.log('📂 Chargement des catégories...');
    this.announceService.getCategories().subscribe({
      next: (cats) => {
        this.categories = cats;
        console.log('✅ Catégories chargées:', this.categories.length, this.categories);
      },
      error: (e) => {
        console.error('❌ Erreur chargement catégories', e);
      }
    });


    
  }

  get quizList(): FormArray {
    return this.announcementForm.get('quizList') as FormArray;
  }

  createQuizGroup(): FormGroup {
    return this.fb.group({
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

  addQuiz(): void {
    this.quizList.push(this.createQuizGroup());
  }

  removeQuiz(index: number): void {
    this.quizList.removeAt(index);
  }

  getQuestions(quizIndex: number): FormArray {
    return this.quizList.at(quizIndex).get('questions') as FormArray;
  }

  addQuestion(quizIndex: number): void {
    this.getQuestions(quizIndex).push(this.createQuestionGroup());
  }

  removeQuestion(quizIndex: number, questionIndex: number): void {
    this.getQuestions(quizIndex).removeAt(questionIndex);
  }

  getResponses(quizIndex: number, questionIndex: number): FormArray {
    return this.getQuestions(quizIndex).at(questionIndex).get('responses') as FormArray;
  }

  addResponse(quizIndex: number, questionIndex: number): void {
    this.getResponses(quizIndex, questionIndex).push(this.createResponseGroup());
  }

  removeResponse(quizIndex: number, questionIndex: number, responseIndex: number): void {
    this.getResponses(quizIndex, questionIndex).removeAt(responseIndex);
  }

  onMainImageSelected(event: any): void {
    this.mainImageFile = event.target.files[0];
    // Mettre à jour le label avec le nom du fichier
    const label = document.getElementById('mainImageLabel');
    if (label && this.mainImageFile) {
      label.textContent = this.mainImageFile.name;
      label.style.color = '#256F86';
      label.style.fontWeight = '600';
    }
  }

  onProductImagesSelected(event: any): void {
    this.productImageFiles = Array.from(event.target.files);
    // Mettre à jour le label avec le nombre de fichiers
    const label = document.getElementById('productImagesLabel');
    if (label && this.productImageFiles.length > 0) {
      label.textContent = `${this.productImageFiles.length} fichier(s) sélectionné(s)`;
      label.style.color = '#256F86';
      label.style.fontWeight = '600';
    }
  }











 // ---------- RECOMPENSES (NOUVELLES) ----------
  get rewards(): FormArray {
    return this.announcementForm.get('recompensesList') as FormArray;
  }

  private createRewardRow(): FormGroup {
    return this.fb.group({
      typeRecompenses: [null, Validators.required],
      amount: [null, Validators.required],
      label: [''],
    }, { validators: this.rewardRowValidator });
  }

  addRewardRow(): void { this.rewards.push(this.createRewardRow()); }
  removeRewardRow(index: number): void { this.rewards.removeAt(index); }

  /** Règles : Points → entier ≥ 0 ; Argent/BonsDachats → montant > 0 */
  private rewardRowValidator = (group: FormGroup) => {
    const type = group.get('typeRecompenses')?.value as TypeRecompenses | null;
    const raw = group.get('amount')?.value;
    const amount = raw === '' || raw == null ? null : Number(raw);

    if (!type) return { rewardRow: 'typeRecompenses requis' };
    if (amount == null || Number.isNaN(amount)) return { rewardRow: 'amount requis' };

    if (type === TypeRecompenses.Points) {
      if (!Number.isInteger(amount) || amount < 0) return { rewardRow: 'Points entier ≥ 0' };
    } else {
      if (!(amount > 0)) return { rewardRow: 'Montant > 0 requis' };
    }
    return null;
  };

  /** Sérialise en RecompenseNew[] (normalisation incluse) */
  private serializeRewards(): RecompenseNew[] {
    return this.rewards.controls.map(ctrl => {
      const v = ctrl.value as any;
      const r = new RecompenseNew({
        typeRecompenses: v.typeRecompenses as TypeRecompenses,
        amount: Number(v.amount),
        label: v.label || undefined,
      });
      // normalisation côté UI
      if (r.typeRecompenses === TypeRecompenses.Points) {
        r.amount = Math.max(0, Math.floor(r.amount));
      } else {
        r.amount = Math.max(r.amount, 0.01);
      }
      return r;
    });
  }


 selectedId?: string;

setSelected(a: announce) {
  this.selectedId = a.idAnnouncement;
}







  onSubmit(): void {
    console.log('📝 Soumission du formulaire...');
    console.log('Formulaire valide ?', !this.announcementForm.invalid);
    console.log('Valeurs du formulaire:', this.announcementForm.value);
    console.log('Erreurs du formulaire:', this.announcementForm.errors);

    if (this.announcementForm.invalid) {
      console.log('❌ Formulaire invalide');
      Object.keys(this.announcementForm.controls).forEach(key => {
        const control = this.announcementForm.get(key);
        if (control?.invalid) {
          console.log(`❌ Champ invalide: ${key}`, control.errors);
        }
      });
      this.announcementForm.markAllAsTouched();
      return;
    }
    const catId = Number(this.announcementForm.get('category.idcategory')?.value);
    console.log('📂 ID Catégorie:', catId);
    if (!catId) {
      console.log('❌ Pas de catégorie sélectionnée');
      alert('Veuillez choisir une catégorie.');
      return;
    }

    // ⚠️ Construire l'objet attendu par le backend (sans DTO)
    const raw = this.announcementForm.value;
    console.log('📦 Données brutes:', raw);

   /* const  a = new announce( {
      announceName: raw.announceName,
      content: raw.content,
      deliveryAddress: raw.deliveryAddress,
      officeAddress: raw.officeAddress,
      testModes: raw.testModes,                 // p.ex. ["HOME_DELIVERY"]
      category: { idcategory: raw.idcategory }, // 👈 clé : category avec idcategory
    

      quizList: raw.quizList,                    // ta structure existante
      recompensesList: this.serializeRewards(),
    });*/







    const formData = new FormData();
    console.log('🔧 Construction du FormData...');
    formData.append('announceData', JSON.stringify(this.announcementForm.value));
    //formData.append('announceData', JSON.stringify(a));
    
    formData.append('quizData', JSON.stringify(this.announcementForm.value.quizList)); // si besoin

    if (this.mainImageFile) {
      console.log('🖼️ Image principale:', this.mainImageFile.name);
      formData.append('image', this.mainImageFile);
    } else {
      console.log('⚠️ Pas d\'image principale');
    }

    if (this.productImageFiles.length > 0) {
      console.log(`🖼️ ${this.productImageFiles.length} images produit`);
      for (let img of this.productImageFiles) {
        formData.append('productImages', img);
      }
    } else {
      console.log('⚠️ Pas d\'images produit');
    }

    console.log('🚀 Envoi de la requête au backend...');
    //this.http.post('http://localhost:8081/Announcement/addAnnounce', formData)
  /* this.announceService.createAnnounce(formData)
      .subscribe({
        next: () => {
          alert('Annonce et quiz ajoutés avec succès');
          this.router.navigate(['/announcements']);
        },
        error: err => console.error(err)
      });*/
    this.announceService.createAnnounce(formData).subscribe({
  next: (res) => {
    console.log('✅ Annonce créée avec succès !', res);
    alert("Annonce et quiz ajoutés avec succès !");
    this.router.navigate(['/annonceur/home']);
  },
  error: (err) => {
    console.error("❌ Erreur lors de la création de l'annonce", err);
    console.error("❌ Détails de l'erreur:", {
      status: err.status,
      statusText: err.statusText,
      error: err.error,
      message: err.message
    });
    //alert("Erreur lors de la création de l'annonce");
    alert(err?.error?.details || err?.error?.error || err?.error?.message || "Erreur lors de la création de l'annonce");

  }
});
  }

  onTestModeChange(event: any): void {
  const testModes: string[] = this.announcementForm.get('testModes')?.value || [];
  if (event.target.checked) {
    testModes.push(event.target.value);
  } else {
    const index = testModes.indexOf(event.target.value);
    if (index !== -1) testModes.splice(index, 1);
  }
  this.announcementForm.patchValue({ testModes });
}
get isOfficeTestingSelected(): boolean {
  return this.announcementForm.get('testModes')?.value.includes('OFFICE_TESTING');
}


 
  logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }

}