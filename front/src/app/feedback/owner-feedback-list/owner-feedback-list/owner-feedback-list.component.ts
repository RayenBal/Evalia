import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { ActivatedRoute, RouterModule,Router } from '@angular/router';
import {
  FeedbackOwnerService,
  
  FeedbackStatsDto,
} from '../../feedback-owner/feedback-owner.service';
import { finalize } from 'rxjs/operators';
import { OwnerFeedbackItemDto } from '../../feedback-owner/feedback-owner.service';
import { AuthService
  
 } from '../../../auth/auth-service/auth.service';
 import { AnnounceServiceService } from '../../../Announce/AnnounceService/announce-service.service';
import { announce } from '../../../Announce/AnnounceModel/announce';
@Component({
  selector: 'app-owner-feedback-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './owner-feedback-list.component.html',
  styleUrl: './owner-feedback-list.component.css'
})
export class OwnerFeedbackListComponent implements OnInit{
 @Input() announcementId?: string;
 constructor(
    public auth: AuthService,private router:Router, private announceSvc: AnnounceServiceService
  ) {}
  loading = false;
  error: string | null = null;

  stats: FeedbackStatsDto | null = null;
  items: OwnerFeedbackItemDto[] = [];

  private route = inject(ActivatedRoute);
  private svc = inject(FeedbackOwnerService);
selectedId?: string;
  ngOnInit(): void {
    this.announcementId ??=
      this.route.snapshot.paramMap.get('id') ??
      this.route.snapshot.paramMap.get('announcementId') ?? undefined;
      this.announceSvc.getMyAnnounces().subscribe(list => {
    this.selectedId = list?.[0]?.idAnnouncement ?? undefined;
  });
    this.refresh();
  }

  refresh(): void {
    if (!this.announcementId) { this.error = 'announcementId manquant'; return; }
    this.loading = true; this.error = null;

    this.svc.stats(this.announcementId)
      .pipe(finalize(() => (this.loading = false)))
      .subscribe({
        next: s => this.stats = s,
        error: err => {
          this.error = err?.error?.message || err?.message || 'Erreur chargement stats';
          console.error('[OwnerFeedbackList] stats error', err);
        }
      });

    this.svc.list(this.announcementId).subscribe({
      next: rows => this.items = rows ?? [],
      error: err => {
        this.error ||= err?.error?.message || err?.message || 'Erreur chargement feedbacks';
        console.error('[OwnerFeedbackList] items error', err);
      }
    });
  }

  star(r: number | null | undefined): string {
    const n = Math.max(0, Math.min(5, r ?? 0));
    return '★★★★★'.slice(0, n) + '☆☆☆☆☆'.slice(n);
  }

  name(it: OwnerFeedbackItemDto): string {
    return [it.panelistFirstname, it.panelistLastname].filter(Boolean).join(' ') || '—';
  }

  trackById = (_: number, it: OwnerFeedbackItemDto) => it.idFeedback;

  back(): void { history.back(); }

      logout() {
    this.auth.clearToken();
    this.router.navigate(['']);
  }
  setSelected(a: announce) {
    this.selectedId = a.idAnnouncement;
  }
}