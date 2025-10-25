import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormArray, FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FeedbackService } from '../feedbackService/feedback.service';
import { AuthService } from '../../auth/auth-service/auth.service';
import { feedback } from '../feedbackModel/feedback';
import { NotificationService } from '../../notifications/notificationService/notification.service';
@Component({
  selector: 'app-feedback-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './feedback-form.component.html',
  styleUrl: './feedback-form.component.css'
})
export class FeedbackFormComponent  implements OnInit{
  @Input({ required: true }) announcementId!: string;
  //@Input({ required: true }) panelistId!: number;
//announceId = '';
  @Output() submitted = new EventEmitter<void>();
  /** event pour rafraîchir la page parente après envoi */

  ratings = [1, 2, 3, 4, 5];
  panelistId = 0;
   existing: feedback | null = null;
   editing = false;                    
  form: FormGroup = this.fb.group({
    rating: [null, [Validators.min(1), Validators.max(5)]],
    comment: ['']
  });
 loading = false;
  constructor(public fb: FormBuilder, private feedbackService: FeedbackService, public auth: AuthService,private notif: NotificationService  ) {}

   ngOnInit(): void {
    if (this.auth.isAuthenticated() && this.auth.isPaneliste) {
      this.feedbackService.getMine(this.announcementId).subscribe(res => {
        if (res.status === 200 && res.body) {
          this.existing = res.body;
          this.form.patchValue({
            rating: res.body.rating ?? null,
            comment: res.body.comment ?? ''
          });
          this.editing = false;
        } else {
          // pas encore de feedback → formulaire vierge en création
          this.existing = null;
          this.form.reset();
          this.editing = true;
        }
      });
    }
  }

  get isEdit(): boolean { return !!this.existing; }

get stars(): string {
    const r = this.existing?.rating ?? 0;
    return '★★★★★'.slice(0, r) + '☆☆☆☆☆'.slice(r);
  }
 enterEdit(): void {
    // préremplir avec l’existant
    if (this.existing) {
      this.form.patchValue({
        rating: this.existing.rating ?? null,
        comment: this.existing.comment ?? ''
      });
    }
    this.editing = true;
  }

  cancelEdit(): void {
    // revenir en lecture (sans perdre l’existant)
    this.editing = false;
    if (!this.existing) {
      // s’il n’y a rien d’existant, rester en édition (création)
      this.editing = true;
    }
  }

 get canSubmit(): boolean {
    return this.auth.isAuthenticated() && this.auth.isPaneliste && this.form.valid && !!this.announcementId;
  }

  private showNewRewardNotifications(startISO: string) {
  this.notif.listMine().subscribe(list => {
    const start = new Date(startISO).getTime();
    const gains = (list ?? []).filter(n =>
      n.type === 'REWARD_GAINED' &&
      n.announcementId === this.announcementId &&
      new Date(n.createdAt).getTime() >= start
    );
    if (gains.length) {
      // message compact
      alert(
        '✅ Vous avez gagné des récompenses :\n\n' +
        gains.map(g => '• ' + g.message).join('\n')
      );
    }
  });
}

  submit(): void {
    if (!this.canSubmit) {
      alert('Connectez-vous en tant que paneliste et remplissez le formulaire.');
      this.form.markAllAsTouched();
      return;
    }
  
   // if (!this.announcementId || !this.panelistId) {
      if (!this.announcementId ) {
      alert('announcementId manquant');
      return;
    }
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

  //  const rating = this.form.value.rating ?? null;
   // const comment = this.form.value.comment ?? null;
const { rating, comment } = this.form.value;
    this.loading = true;

    //this.feedbackService.createSimple(this.announcementId, this.panelistId, rating, comment)
    //this.feedbackService.createSimple(this.announcementId,  rating ?? null, comment ?? null) 
//.subscribe({
const startISO = new Date().toISOString();
   const call = this.isEdit
     ? this.feedbackService.updateMine(this.announcementId, rating ?? null, comment ?? null)
     : this.feedbackService.createSimple(this.announcementId, rating ?? null, comment ?? null);

   call.subscribe({
     next: fb => {
       this.loading = false;
       this.existing = fb;
       this.editing = false;
    
this.showNewRewardNotifications(startISO);

       alert(this.isEdit ? 'Feedback mis à jour.' : 'Feedback enregistré.');
       this.form.reset();
       this.submitted.emit();
     },
     error: (err) => {
       this.loading = false
       console.error(err);
       alert('❌ Erreur lors de l’envoi du feedback');
     }
   });
 }

}