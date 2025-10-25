import { Component } from '@angular/core';
import { FormBuilder, Validators ,ReactiveFormsModule} from '@angular/forms';
import { AuthService } from '../../auth-service/auth.service';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-forgot-password',
  standalone: true,
  imports: [ReactiveFormsModule,CommonModule],
  templateUrl: './forgot-password.component.html',
  styleUrl: './forgot-password.component.css'
})
export class ForgotPasswordComponent {
private baseUrl = 'http://localhost:8081/api/v1/auth/password';
loading = false;
  infoMsg = '';
  errorMsg = '';

  form = this.fb.group({
    email: ['', [Validators.required, Validators.email]]
  });

  constructor(private fb: FormBuilder, private auth: AuthService) {}

  submit() {
    this.infoMsg = '';
    this.errorMsg = '';
    if (this.form.invalid) return;

    const { email } = this.form.value as { email: string };
    this.loading = true;
    this.auth.forgotPassword(email).subscribe({
      next: () => { this.loading = false; this.infoMsg = 'Un e-mail avec le code vous a été envoyé.'; },
      error: (e) => { this.loading = false; this.errorMsg = e.message || 'Erreur'; }
    });
  }
}
