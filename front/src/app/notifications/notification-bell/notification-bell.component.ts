import { CommonModule } from '@angular/common';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { NotificationService } from '../notificationService/notification.service';
import { Observable, Subscription } from 'rxjs';
import { Notification } from '../notificationModel/notification';
@Component({
  selector: 'app-notification-bell',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
  <div class="bell-wrapper">
    <button class="icon-btn" (click)="toggle()"
      aria-label="Notifications">
      <i class="bi bi-bell"></i>
      <span *ngIf="(unseen$ | async) as c" class="badge" [hidden]="c===0">{{ c }}</span>
    </button>

    <div class="dropdown" *ngIf="(open$ | async)">
      <div class="drop-head">
        <strong>Notifications</strong>
        <button class="link" (click)="markAll()">Tout marquer comme lu</button>
      </div>

      <div class="list" *ngIf="(items$ | async) as items; else empty">
        <div *ngFor="let n of items" class="row" [class.unseen]="!n.seen" (click)="open(n)">
          <div class="title">
            <i class="bi" [ngClass]="{
              'bi-gift': n.type==='REWARD_GAINED',
              'bi-chat-left-text': n.type==='FEEDBACK_RECEIVED'
            }"></i>
            <span>{{ n.message }}</span>
          </div>
          <div class="meta">
            <small>{{ n.createdAt | date:'short' }}</small>
          </div>
        </div>
      </div>

      <ng-template #empty>
        <div class="empty">Aucune notification</div>
      </ng-template>
    </div>
  </div>
  `,
  styles: [`
  .bell-wrapper{ position:relative; }
  .icon-btn{
    position:relative; border:none; background:transparent; cursor:pointer;
    font-size:1.6rem; padding:0; line-height:1; color:#333;
  }
  .badge{
    position:absolute; top:-6px; right:-8px; min-width:20px; height:20px;
    background:linear-gradient(135deg,#FF6B6B,#FF8E53);
    color:#fff; border-radius:999px; display:flex; align-items:center; justify-content:center;
    font-size:.75rem; font-weight:700; padding:0 6px; box-shadow:0 2px 8px rgba(255,107,107,.4);
  }
  .dropdown{
    position:absolute; right:0; top:120%;
    width:360px; max-height:70vh; overflow:auto;
    background:#fff; border:1px solid #eee; border-radius:12px;
    box-shadow:0 12px 30px rgba(0,0,0,.12); z-index:9999;
  }
  .drop-head{
    display:flex; align-items:center; justify-content:space-between;
    padding:.75rem 1rem; border-bottom:1px solid #eee;
  }
  .drop-head .link{ border:none; background:none; color:#256F86; cursor:pointer; }
  .list .row{ padding:.75rem 1rem; border-bottom:1px solid #f4f4f4; cursor:pointer;}
  .list .row.unseen{ background:#F6FFED; }
  .list .row:hover{ background:#F9FAFB; }
  .title{ display:flex; gap:.5rem; align-items:center; color:#333; }
  .meta{ color:#777; }
  .empty{ padding:1rem; color:#999; text-align:center; }
  `]
})
export class NotificationBellComponent implements OnInit, OnDestroy {
  items$!: Observable<Notification[]>;
  unseen$!: Observable<number>;
  open$!: Observable<boolean>;
openValue = false;
  private sub?: Subscription;

  constructor(private ns: NotificationService, private router: Router) {}

  ngOnInit(): void {
    // branche les observables
    this.items$ = this.ns.items$;
    this.unseen$ = this.ns.unseen$;
    this.open$   = this.ns.open$;
 this.sub = this.open$.subscribe(v => this.openValue = v);

  this.ns.ensureConnected(); // ✅ au lieu de initSse()
    // démarre la connexion (liste initiale + SSE)
   // this.ns.initSse();
  }

  ngOnDestroy(): void {
    // si ce composant vit partout (navbar globale) on ne détruit pas le SSE.
    // si c'était un composant transient, on appellerait ns.destroySse()
  }

  /*toggle() { this.ns.setOpen(!(this as any).openValue);  this.sub?.unsubscribe();
    this.sub = this.open$.subscribe(v => (this as any).openValue = v);
    this.ns.setOpen(!(this as any).openValue);
  }*/
  toggle() {
  this.ns.setOpen(!this.openValue); // ✅ simple
}


  markAll() { this.ns.markAllSeen().subscribe(); }

  open(n: Notification) {
    if (!n.seen) this.ns.markSeen(n.id).subscribe();
    // si la notif cible une annonce → navigue
    if (n.announcementId) {
      this.router.navigate(['/announcement/details', n.announcementId]);
      this.ns.setOpen(false);
    }
  }
}