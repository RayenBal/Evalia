import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, AbstractControl,ReactiveFormsModule,FormGroup } from '@angular/forms';
import { AuthService, RegisterRequest, AuthResponse }  from '../auth-service/auth.service';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css'
})
export class SignUpComponent implements OnInit{
private baseUrl = 'http://localhost:8081/api/v1/auth';

loading = false;
  errorMsg = '';
  infoMsg = '';
 showPw = false;
  showPw2 = false;
  ageRanges = ['18_25','26_35','36_45','46_60','60_plus'] as const;
  //roles = ['PANELISTE','ANNONCEUR']; // ⚠️ Adapter aux noms présents en DB (RoleRepository)

  /*form = this.fb.group({
    firstname: ['', Validators.required],
    lastname: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
    confirm: ['', Validators.required], 
    numTelephone: ['', Validators.required],
    typeUser: ['Paneliste' as 'Paneliste'|'Announceur', Validators.required],
    companyName: [''],
    jobTitle: [''],
    //age: [null],
    ageRange: ['']
    
   // role: ['PANELISTE', Validators.required]
  });*/


  form = this.fb.group(
    {
      firstname: ['', Validators.required],
      lastname: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(8)]],
      confirm: ['', Validators.required],                        // 👈 ajout
      numTelephone: ['', Validators.required],
      typeUser: ['Paneliste' as 'Paneliste'|'Announceur', Validators.required],
      companyName: [''],
      jobTitle: [''],
      ageRange: [''],
      iban: [''],
        deliveryAddress: ['']
    },
    { validators: [SignUpComponent.mustMatch('password', 'confirm')] } // 👈 validation form
  );
  // fichier PDF (Announceur)
  registreCommerceFile: File | null = null;
  fileError = '';
  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router) {}
static mustMatch(a: string, b: string) {
    return (group: FormGroup) => {
      const pa = group.get(a)?.value ?? '';
      const pb = group.get(b)?.value ?? '';
      return pa && pb && pa === pb ? null : { mismatch: true };
    };
  }
  ngOnInit(): void {
    // Validators conditionnels selon typeUser
    this.form.get('typeUser')!.valueChanges.subscribe(tp => {
      const isPanel = tp === 'Paneliste';
      this.setRequired(this.form.get('companyName')!, !isPanel);
      this.setRequired(this.form.get('jobTitle')!, isPanel);
         this.setRequired(this.form.get('deliveryAddress')!, isPanel);   

      this.setRequired(this.form.get('ageRange')!, isPanel);
      this.setRequired(this.form.get('iban')!, isPanel);

      // Rôle par défaut selon type choisi (adapter si besoin)
     // this.form.get('role')!.setValue(isPanel ? 'PANELISTE' : 'ANNONCEUR');
    });
    // Initial apply
    this.form.get('typeUser')!.updateValueAndValidity({ emitEvent: true });
  }
onFileChange(evt: Event) {
    const input = evt.target as HTMLInputElement;
    this.fileError = '';
    if (!input.files || input.files.length === 0) {
      this.registreCommerceFile = null;
      return;
    }
    const f = input.files[0];
    const name = (f.name || '').toLowerCase();
    const type = (f.type || '').toLowerCase();
    if (!type.includes('pdf') && !name.endsWith('.pdf')) {
      this.fileError = 'Le fichier doit être un PDF.';
      this.registreCommerceFile = null;
      return;
    }
    this.registreCommerceFile = f;
  }
  private setRequired(ctrl: AbstractControl, required: boolean) {
    if (required) ctrl.addValidators(Validators.required);
    else ctrl.removeValidators(Validators.required);
    ctrl.updateValueAndValidity();
  }

  submit() {
    this.errorMsg = '';
    this.infoMsg = '';
    if (this.form.invalid) return;

    const body: RegisterRequest = this.form.value as any;


     // PDF obligatoire si Announceur
    if (body.typeUser === 'Announceur' && !this.registreCommerceFile) {
      this.fileError = 'obligation de copie de registre de commerce';
      return;
    } 
     const fd = this.auth.buildRegisterFormData(body, this.registreCommerceFile);

    this.loading = true;
    this.auth.registerFormData(fd).subscribe({

   // this.auth.register(body).subscribe({
      next: (res: AuthResponse) => {
        this.loading = false;
        this.infoMsg = res.message || 'Inscription réussie.';
        // Option : rediriger vers /login
        this.router.navigateByUrl('/login');
      },
      error: (e) => { this.loading = false; this.errorMsg = e.message || 'Erreur'; }
    });
  }
}
