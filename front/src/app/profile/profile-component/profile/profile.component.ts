import { Component, OnInit } from '@angular/core';
import { CommonModule ,Location} from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, FormGroup,AbstractControl  } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ProfileService } from '../../profileService/profile.service';
import { UpdateMe ,MeProfile} from  '../../profile.service.types';
import { AuthService } from '../../../auth/auth-service/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  
  get isAnnonceur() { return this.auth.userType === 'Announceur'; }
  get isPaneliste() { return this.auth.userType === 'Paneliste'; }

  logout() {
    this.auth.clearToken();
    this.router.navigate(['/login']);
  }
  goBack() { this.loc.back(); }
  me: MeProfile | null = null;
  loading = false;
  error = '';

  info = '';
  saving = false;

  ageRanges = ['18_25','26_35','36_45','46_60','60_plus'] as const;
    original = {
    iban: '' as string,
    deliveryAddress: '' as string
  };
showCur = false;
showNew = false;
showConf = false;
  form = this.fb.group({
    firstname: ['', Validators.required],
    lastname: ['', Validators.required],
    numTelephone: [''],
    companyName: [''],
    jobTitle: [''],
    ageRange: [''],
     // IBAN : pattern souple côté front (contrôle fort côté back)
   iban: ['', [
  // accepte espaces pendant la saisie
  Validators.pattern(/^[A-Za-z]{2}\d{2}[A-Za-z0-9 ]{11,30}$/),
  Validators.maxLength(34)
]],

    // Adresse livraison (max 255, le "required" sera appliqué dynamiquement si Paneliste)
    deliveryAddress: ['', [Validators.maxLength(255)]],
  });

  // ——— Mot de passe
  pwdSaving = false;
  pwdInfo = '';
  pwdError = '';
  pwdForm = this.fb.group(
    {
      currentPassword: ['', Validators.required],
      newPassword: ['', [Validators.required, Validators.minLength(8)]],
      confirm: ['', Validators.required],
    },
    {
      // 2 validateurs groupe :
      validators: [
        ProfileComponent.mustMatch('newPassword', 'confirm'),
        ProfileComponent.different('currentPassword', 'newPassword'),
      ],
    }
  );

  constructor(
    private fb: FormBuilder,
    private svc: ProfileService,
    public auth: AuthService,
    public router: Router, private loc: Location
  ) {}
private setRequired(ctrl: AbstractControl | null, required: boolean) {
  if (!ctrl) return;
  if (required) ctrl.addValidators(Validators.required);
  else ctrl.removeValidators(Validators.required);
  ctrl.updateValueAndValidity({ emitEvent: false });
}
  /** newPassword === confirm */
  static mustMatch(a: string, b: string) {
    return (group: FormGroup) => {
      const va = group.get(a)?.value;
      const vb = group.get(b)?.value;
      return va && vb && va === vb ? null : { mismatch: true };
    };
  }

  /** newPassword !== currentPassword */
  static different(a: string, b: string) {
    return (group: FormGroup) => {
      const va = group.get(a)?.value ?? '';
      const vb = group.get(b)?.value ?? '';
      return va && vb && va === vb ? { same: true } : null;
    };
  }
selectedId?: string;
  ngOnInit(): void { this.load(); }



formatIban(v?: string | null): string {
    if (!v) return '';
    return v.replace(/\s+/g, '').toUpperCase().replace(/(.{4})/g, '$1 ').trim();
  }
  get ibanChanged(): boolean {
    const cur = (this.form.get('iban')?.value || '').replace(/\s+/g, '').toUpperCase();
    return cur !== (this.original.iban || '');
  }
  get addressChanged(): boolean {
    const cur = (this.form.get('deliveryAddress')?.value || '');
    return cur !== (this.original.deliveryAddress || '');
  }




  load(): void {
    this.loading = true; this.error = '';
    this.svc.me().subscribe({
      next: (m) => {
        this.me = m; this.loading = false;
        this.form.reset({
          firstname: m.firstname || '',
          lastname: m.lastname || '',
          numTelephone: m.numTelephone || '',
          companyName: m.companyName || '',
          jobTitle: m.jobTitle || '',
          ageRange: m.ageRange || '',
            iban: (m.iban ?? ''),                // valeur dans l’input si existante
  deliveryAddress: (m.deliveryAddress ?? ''),
        });
      this.original = {
          iban: (m.iban || '').replace(/\s+/g, '').toUpperCase(),
          deliveryAddress: m.deliveryAddress || ''
        };

        // ✅ si Paneliste → rendre IBAN + adresse requis côté front
        const isPanel = m.typeUser === 'Paneliste';
this.setRequired(this.form.get('iban'), m.typeUser === 'Paneliste');
        this.setRequired(this.form.get('deliveryAddress'),  m.typeUser === 'Paneliste');
      },
      error: (e) => { this.error = e?.message || 'Erreur chargement profil'; this.loading = false; }
    });
  }

  save(): void {
    if (!this.me) return;
    this.saving = true; this.info = this.error = '';
    const patch: UpdateMe = this.form.value as any;
 if (patch.iban) patch.iban = patch.iban.replace(/\s+/g, '').toUpperCase();
     this.svc.updateMe(patch).subscribe({
     /* next: (m) => { this.me = m; this.saving = false; this.info = 'Profil mis à jour.'; },*/
      next: (m) => {
  this.me = m;
  this.saving = false;
  this.info = 'Profil mis à jour.';
  // maj des valeurs de référence (anciennes)
  this.original.iban = (m.iban || '').replace(/\s+/g, '').toUpperCase();
  this.original.deliveryAddress = m.deliveryAddress || '';
},
      error: (e) => { this.saving = false; this.error = e?.message || 'Erreur mise à jour'; }
    });
  }

  /** Mappe proprement le message d'erreur back → message lisible */
  private pickPwdError(err: any): string {
    const raw =
      (typeof err?.error === 'string' ? err.error : err?.error?.message) ||
      err?.message ||
      '';

    if (/actuel incorrect/i.test(raw)) {
      return 'Votre ancien mot de passe est incorrect.';
    }
    if (/doit être différent/i.test(raw)) {
      return 'Le nouveau mot de passe doit être différent de l’ancien.';
    }
    if (err?.status === 400 && !raw) {
      return 'Requête invalide.';
    }
    return raw || 'Erreur changement de mot de passe';
  }

  changePassword(): void {
    this.pwdInfo = this.pwdError = '';

    // Affiche immédiatement l’erreur si le nouveau = ancien
    if (this.pwdForm.hasError('same')) {
      this.pwdError = 'Le nouveau mot de passe doit être différent de l’ancien.';
      this.pwdForm.markAllAsTouched();
      return;
    }
    if (this.pwdForm.invalid) return;

    const { currentPassword, newPassword } = this.pwdForm.value as any;
    this.pwdSaving = true;
    this.svc.changePassword(currentPassword, newPassword).subscribe({
      next: () => {
        this.pwdSaving = false;
        this.pwdInfo = 'Mot de passe modifié. Vous allez être déconnecté.';
        setTimeout(() => { this.auth.clearToken(); location.href = '/login'; }, 1200);
      },
      error: (e) => {
        this.pwdSaving = false;
        this.pwdError = this.pickPwdError(e);
      }
    });
  }

  /*goBack() {
    if (window.history.length > 1) { history.back(); return; }
    this.auth.isPaneliste ? this.router.navigate(['/paneliste/home'])
                          : this.router.navigate(['/annonceur/home']);
  }*/
}