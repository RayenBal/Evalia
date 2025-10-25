import { Component, OnInit  } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FeedbackService } from '../feedbackService/feedback.service';
import { feedback } from '../feedbackModel/feedback';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../auth/auth-service/auth.service';
@Component({
  selector: 'app-feedback-list',
  standalone: true,
  imports: [CommonModule,RouterModule],
  templateUrl: './feedback-list.component.html',
  styleUrl: './feedback-list.component.css'
})
export class FeedbackListComponent implements OnInit {
  id!: string;                 // announcementId lu depuis l’URL
  items: feedback[] = [];
  loading = false;
panelistId = 0;
  constructor(
    private feedbacks: FeedbackService,
    private route: ActivatedRoute, public auth: AuthService,private router:Router
  ) {}

  ngOnInit(): void {
    // Simple et suffisant : lecture une fois à l'init
    this.id = this.route.snapshot.paramMap.get('id') ?? '';
    if (!this.id) return;

    this.loading = true;
    this.feedbacks.listByAnnouncement(this.id).subscribe({
      next: d => { this.items = d ?? []; this.loading = false; },
      error: e => { console.error(e); this.loading = false; }
    });
  }

    logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }

}