import { Component } from '@angular/core';
import { FormBuilder, Validators ,ReactiveFormsModule} from '@angular/forms';
import {AuthResponse, AuthService } from '../auth-service/auth.service';
import { Router ,RouterModule,ActivatedRoute} from '@angular/router';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-sign-in',
  standalone: true,
  imports: [CommonModule, RouterModule,ReactiveFormsModule],
  templateUrl: './sign-in.component.html',
  styleUrl: './sign-in.component.css'
})
export class SignInComponent {
  private baseUrl = 'http://localhost:8081/api/v1/auth';

  loading = false;
  errorMsg = '';
  infoMsg = '';
  showOtp = false;

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(8)]],
  });

  otpForm = this.fb.group({
    code: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
  });

  constructor(private fb: FormBuilder, private auth: AuthService, private router: Router, private route: ActivatedRoute) {}
private goAfterAuth() {
    // 1) priorité au retour demandé dans l'URL
    const r = this.route.snapshot.queryParamMap.get('r');
    if (r) { 
      console.log('🔄 Redirection vers URL demandée:', r);
      this.router.navigateByUrl(r); 
      return; 
    }

    // 2) sinon, routing par type utilisateur
    const t = this.auth.userType; // ← injecté dans le JWT par ton backend
    console.log('🔄 Type utilisateur détecté:', t);
    //this.router.navigateByUrl(t === 'Announceur' ? '/annonceur/home' : '/');
    if (t === 'Paneliste') {
      console.log('➡️ Redirection vers /paneliste/home');
      this.router.navigate(['/paneliste/home']);
    }
    else if (t === 'Announceur') {
      console.log('➡️ Redirection vers /annonceur/home');
      this.router.navigate(['/annonceur/home']);
    }
    else {
      console.log('➡️ Redirection vers la page d\'accueil');
      this.router.navigate(['/']);
    }
  }
  submitLogin() {
    this.errorMsg = '';
    this.infoMsg = '';
    if (this.form.invalid) {
      console.log('❌ Formulaire invalide', this.form.errors);
      return;
    }

    const { email, password } = this.form.value as { email: string; password: string };
    console.log('🔐 Tentative de connexion pour:', email);
    this.loading = true;
    this.auth.login(email, password).subscribe({
      next: (res: AuthResponse) => {
        console.log('✅ Réponse du serveur:', res);
        this.loading = false;
        if (res.pending) {
          console.log('📧 OTP requis');
          this.showOtp = true;
          this.infoMsg = res.message || 'Code OTP envoyé sur votre e-mail';
        } else {
          console.log('✅ Token reçu:', res.token);
          this.auth.setToken(res.token);
          console.log('👤 Type utilisateur:', this.auth.userType);
          this.goAfterAuth();
        }
      },
      error: (e:any) => { 
        console.error('❌ Erreur de connexion:', e);
        this.loading = false; 
        this.errorMsg = e.message || 'Erreur de connexion'; 
      }
    });
  }

  submitOtp() {
    this.errorMsg = '';
    this.infoMsg = '';
    if (this.otpForm.invalid || this.form.get('email')?.invalid) return;

    const email = this.form.get('email')!.value as string;
    const code = this.otpForm.get('code')!.value as string;

    this.loading = true;
    this.auth.confirmOtp(email, code).subscribe({
      next: (res) => {
        this.loading = false;
        this.auth.setToken(res.token);
       // this.router.navigateByUrl('/');
       (this.auth.userType === 'Paneliste')
      ? this.router.navigate(['/paneliste/home'])
      : this.router.navigate(['/annonceur/home']);
      },
      error: (e) => { this.loading = false; this.errorMsg = e.message || 'Erreur OTP'; }
    });
  }


  
}
