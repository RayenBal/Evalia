import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AnnounceServiceService } from '../AnnounceService/announce-service.service';
import { announce } from '../AnnounceModel/announce';
import { TypeRecompenses, RecompenseNew } from '../../recompenses/recompensesModel/recompenses';
import { FormArray } from '@angular/forms';
import { QuizService } from '../../quiz/quizService/quiz.service';
import { AuthService } from '../../auth/auth-service/auth.service';
@Component({
  selector: 'app-update-announce',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule, RouterModule],
  templateUrl: './update-announce.component.html',
  styleUrl: './update-announce.component.css'
})
export class UpdateAnnounceComponent implements OnInit{
baseUrl = 'http://localhost:8081/Announcement';
announceForm!: FormGroup;
  announceId!: string;
    currentMainImage?: string;           // ex: "1234_abcd.png"
  currentProductImages: string[] = [];
  mainImageFile?: File;
  productImageFiles: File[] = [];
 announce: (announce & any) | null = null;
  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private announceService: AnnounceServiceService,
    private quizService: QuizService,
    public auth: AuthService
  ) {}


  // === Rewards helpers ===
get rewards(): FormArray {
return this.announceForm.get('recompensesList') as FormArray;
}


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


private createRewardRow(init?: Partial<RecompenseNew & { idRecompense?: string }>): FormGroup {
return this.fb.group(
{
idRecompense: [init?.['idRecompense'] ?? null],
typeRecompenses: [init?.typeRecompenses ?? null, Validators.required],
amount: [init?.amount ?? null, Validators.required],
label: [init?.label ?? ''],
},
{ validators: this.rewardRowValidator }
);
}


addRewardRow(): void {
this.rewards.push(this.createRewardRow());
}


removeRewardRow(index: number): void {
this.rewards.removeAt(index);
}


/** Normalise la liste pour l'API (conserve idRecompense si présent) */
private serializeRewards(): Array<Partial<RecompenseNew> & { idRecompense?: string }> {
return this.rewards.controls.map(ctrl => {
const v = ctrl.value as any;
const out: any = {
idRecompense: v.idRecompense || undefined,
typeRecompenses: v.typeRecompenses as TypeRecompenses,
amount: Number(v.amount),
label: v.label || undefined,
};
// Normalisation côté UI
if (out.typeRecompenses === TypeRecompenses.Points) {
out.amount = Math.max(0, Math.floor(out.amount));
} else {
out.amount = Math.max(out.amount, 0.01);
}
return out;
});
}
 /*  ngOnInit(): void {
    this.announceId = this.route.snapshot.paramMap.get('id') || '';
    this.announceForm = this.fb.group({
      announceName: ['', Validators.required],
      content: ['', Validators.required],
      officeAddress: [''],
      deliveryAddress: [''],
      recompensesList: this.fb.array([])
    });

    // Pré-remplir + récupérer les noms d’images existantes pour l’aperçu
    this.announceService.getAnnounce(this.announceId).subscribe((data: announce & any) => {
      this.announceForm.patchValue({
        announceName: data.announceName,
        content: data.content,
        officeAddress: data.officeAddress,
        deliveryAddress: data.deliveryAddress
      });

      this.currentMainImage = data.image || undefined;
      this.currentProductImages = (data.productImages || '')
        .split(',')
        .map((s: string) => s.trim())
        .filter((s: string) => !!s);
        

        if (Array.isArray(data.recompensesList)) {
      const arr = this.announceForm.get('recompensesList') as FormArray;
      data.recompensesList.forEach((r: any) => {
        arr.push(this.createRewardRow({
          idRecompense: r.idRecompense,           // supposé renvoyé par le back
          typeRecompenses: r.typeRecompenses,
          amount: Number(r.amount),
          label: r.label
        }));
      });
    }
    });
  }*/
ngOnInit(): void {
    this.announceId = this.route.snapshot.paramMap.get('id') || '';
    this.announceForm = this.fb.group({
      announceName: ['', Validators.required],
      content: ['', Validators.required],
      officeAddress: [''],
      deliveryAddress: [''],
      recompensesList: this.fb.array([]),
    });

    this.loadAnnounce();
  }

  private loadAnnounce(): void {
    this.announceService.getAnnounce(this.announceId).subscribe((data: announce & any) => {
      // pour la section Quiz
      data.quizList = data.quizList ?? [];
      this.announce = data;

      // champs simples
      this.announceForm.patchValue({
        announceName: data.announceName,
        content: data.content,
        officeAddress: data.officeAddress,
        deliveryAddress: data.deliveryAddress,
      });

      // images actuelles
      this.currentMainImage = data.image || undefined;
      this.currentProductImages = (data.productImages || '')
        .split(',')
        .map((s: string) => s.trim())
        .filter((s: string) => !!s);

      // pré-remplir récompenses (on nettoie d'abord)
      const arr = this.announceForm.get('recompensesList') as FormArray;
      arr.clear();
      if (Array.isArray(data.recompensesList)) {
        data.recompensesList.forEach((r: any) => {
          arr.push(this.createRewardRow({
            idRecompense: r.idRecompense,
            typeRecompenses: r.typeRecompenses,
            amount: Number(r.amount),
            label: r.label
          }));
        });
      }
    });
  }

  // =============== Quiz helpers (affichage/actions) ===============
  getQuizId(q: any): string {
    return q?.idQuiz ?? q?.id ?? q?.uuid ?? '';
  }
  trackByQuiz = (_: number, q: any) => this.getQuizId(q);

  deleteQuiz(quizId: string): void {
    if (!quizId) return;
    if (!confirm('Voulez-vous vraiment supprimer ce quiz ?')) return;

    this.quizService.deleteQuiz(quizId).subscribe({
      next: () => {
        // retire localement et recharge proprement
        if (this.announce?.quizList) {
          this.announce.quizList = this.announce.quizList.filter((q: any) => this.getQuizId(q) !== quizId);
        }
        this.loadAnnounce();
        alert('Quiz supprimé avec succès ✅');
      },
      error: (err) => {
        console.error('Erreur suppression quiz', err);
        alert('❌ Erreur lors de la suppression du quiz');
      }
    });
  }
  // util
  getImageUrl(fileName?: string): string {
    return fileName ? `${this.baseUrl}/downloadannounce/${fileName}` : '';
  }

  onMainImageSelected(event: any): void {
    this.mainImageFile = event.target.files?.[0];
  }

  onProductImagesSelected(event: any): void {
    this.productImageFiles = Array.from(event.target.files || []);
  }

  onSubmit(): void {
    if (this.announceForm.invalid) {
      alert('Veuillez remplir tous les champs obligatoires.');
      return;
    }

    // 🔹 Construire le payload en remplaçant le FormArray par une liste normalisée
const raw = this.announceForm.value;
const payload = {
...raw,
recompensesList: this.serializeRewards(),
};

    const formData = new FormData();
    formData.append('announceData', JSON.stringify(this.announceForm.value));

    // si l'utilisateur a choisi une nouvelle image principale, on l'envoie
    if (this.mainImageFile) {
      formData.append('image', this.mainImageFile);
    }
    // si l'utilisateur a choisi de nouvelles images produits, on les envoie
    for (const img of this.productImageFiles) {
      formData.append('productImages', img);
    }

    // 👉 si aucune nouvelle image n’est envoyée, le back gardera les anciennes (ton code le fait déjà)

    this.announceService.updateAnnounceWithImages(this.announceId, formData).subscribe({
      next: () => {
        alert('Annonce mise à jour avec succès ✅');
        this.router.navigate(['/annonceur/home']);
      },
      error: (err) => {
        console.error('Erreur update', err);
        alert('❌ Erreur lors de la mise à jour');
      }
    });
  }
    onCancel(): void {
    this.router.navigate(['/annonceur/home']);
  }

 getRowError(i: number): string | null {
  const g = this.rewards.at(i) as FormGroup;
  return (g.errors?.['rewardRow'] as string) ?? null;
}

  logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }


}
