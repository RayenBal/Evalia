import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators ,ReactiveFormsModule} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../auth-service/auth.service';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css'
})
export class ResetPasswordComponent implements OnInit {
  private baseUrl = 'http://localhost:8081/api/v1/auth/password';

  loading = false;
  infoMsg = '';
  errorMsg = '';
  codeFromUrl: string | null = null;

  form = this.fb.group({
    code: ['', [Validators.required, Validators.pattern(/^\d{6}$/)]],
    newPassword: ['', [Validators.required, Validators.minLength(8)]],
  });

  constructor(private fb: FormBuilder, private route: ActivatedRoute, private auth: AuthService, private router: Router) {}

  ngOnInit(): void {
    this.codeFromUrl = this.route.snapshot.queryParamMap.get('code');
    if (this.codeFromUrl) this.form.get('code')!.setValue(this.codeFromUrl);
  }

  submit() {
    this.infoMsg = '';
    this.errorMsg = '';
    if (this.form.invalid) return;

    const { code, newPassword } = this.form.value as { code: string; newPassword: string };
    this.loading = true;
    this.auth.resetPassword(code, newPassword).subscribe({
      next: () => {
        this.loading = false;
        this.infoMsg = 'Mot de passe réinitialisé. Vous pouvez vous connecter.';
        setTimeout(() => this.router.navigateByUrl('/login'), 1200);
      },
      error: (e) => { this.loading = false; this.errorMsg = e.message || 'Erreur'; }
    });
  }
}