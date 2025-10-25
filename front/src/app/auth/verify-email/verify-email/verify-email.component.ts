import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { AuthService } from '../../auth-service/auth.service';
import { CommonModule } from '@angular/common';
@Component({
  selector: 'app-verify-email',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './verify-email.component.html',
  styleUrl: './verify-email.component.css'
})
export class VerifyEmailComponent implements OnInit {
  private baseUrl = 'http://localhost:8081/api/v1/auth';

  loading = true;
  message = '';
  error = '';

  constructor(private route: ActivatedRoute, private auth: AuthService) {}

  ngOnInit(): void {
    const code = this.route.snapshot.queryParamMap.get('code');
    if (!code) { this.loading = false; this.error = 'Code manquant'; return; }
    this.auth.verifyEmail(code).subscribe({
      next: (txt) => { this.loading = false; this.message = txt || 'E-mail vérifié.'; },
      error: (e) => { this.loading = false; this.error = e.message || 'Erreur'; }
    });
  }
}
