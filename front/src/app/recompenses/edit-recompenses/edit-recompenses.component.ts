import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { RecompensesService } from '../recompensesService/recompenses.service';
import { TypeRecompenses } from '../recompensesModel/recompenses';
import { AuthService } from '../../auth/auth-service/auth.service';

@Component({
  selector: 'app-edit-recompenses',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './edit-recompenses.component.html',
  styleUrl: './edit-recompenses.component.css'
})
export class EditRecompensesComponent implements OnInit{

 form!: FormGroup;
  recId = '';
  announceId?: string | null;         // pour revenir aux détails de l'annonce
  typeOptions = Object.values(TypeRecompenses);

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private svc: RecompensesService,
    public auth:AuthService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      idRecompense: [''],
      typeRecompenses: [null, Validators.required],
      amount: [null, Validators.required],
      label: ['']
    }, { validators: this.rewardValidator });

    this.recId = this.route.snapshot.paramMap.get('id') || '';
    this.announceId = this.route.snapshot.queryParamMap.get('announceId');

    this.svc.getRecompensesById(this.recId).subscribe({
      next: (r: any) => {
        this.form.patchValue({
          idRecompense: r.idRecompense ?? r.id ?? '',
          typeRecompenses: r.typeRecompenses,
          amount: Number(r.amount),
          label: r.label || ''
        });
      },
      error: (e) => console.error('load reward error', e)
    });
  }

  // Points: entier >= 0 ; autres: > 0
  private rewardValidator = (group: FormGroup) => {
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

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }
    const payload = {
      typeRecompenses: this.form.value.typeRecompenses,
      amount: Number(this.form.value.amount),
      label: this.form.value.label || null
    };
    this.svc.updateRecompenses(this.recId, payload).subscribe({
      next: () => {
        alert('Récompense mise à jour ✅');
        if (this.announceId) {
          this.router.navigate(['/announcement/details', this.announceId]);
        } else {
          this.router.navigate(['/announces']);
        }
      },
      error: (e) => {
        console.error('update error', e);
        alert('❌ Erreur lors de la mise à jour');
      }
    });
  }

  onCancel(): void {
    if (this.announceId) {
      this.router.navigate(['/announcement/details', this.announceId]);
    } else {
      this.router.navigate(['/announces']);
    }
  }

  get formError(): string | null {
    return (this.form.errors?.['rewardRow'] as string) ?? null;
  }
     logout() {
    this.auth.clearToken();
    this.router.navigate(['/login']);
  }
}