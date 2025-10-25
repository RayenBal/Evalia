import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { NotificationService } from '../notificationService/notification.service';
import { Observable } from 'rxjs';
import { Notification } from '../notificationModel/notification';

@Component({
  selector: 'app-notification-page',
  standalone: true,
  imports: [CommonModule],
  template: `
  <section class="wrap">
    <h2>Mes notifications</h2>
    <button class="mark" (click)="markAll()">Tout marquer comme lu</button>

    <div *ngIf="(items$ | async) as items; else loading">
      <article *ngFor="let n of items" class="item" [class.unseen]="!n.seen">
        <div class="msg">{{ n.message }}</div>
        <div class="meta">
          <small>{{ n.createdAt | date:'short' }}</small>
          <button *ngIf="!n.seen" (click)="mark(n.id)">Marquer lu</button>
        </div>
      </article>
      <p *ngIf="!items.length" class="empty">Aucune notification</p>
    </div>
    <ng-template #loading>Chargement…</ng-template>
  </section>
  `,
  styles: [`
    .wrap{ max-width:840px; margin:2rem auto; padding:0 1rem; }
    .mark{ margin:.5rem 0 1rem; }
    .item{ border:1px solid #eee; border-radius:12px; padding:1rem; margin-bottom:.75rem; }
    .item.unseen{ background:#F6FFED; }
    .msg{ color:#333; margin-bottom:.25rem; }
    .meta{ display:flex; gap:1rem; align-items:center; color:#777; }
    .empty{ color:#999; }
  `]
})
export class NotificationPageComponent implements OnInit {
  items$!: Observable<Notification[]>;
  constructor(private ns: NotificationService) {}
  ngOnInit(): void {
    this.items$ = this.ns.items$;
    this.ns.listMine().subscribe();       // refresh immédiat
    this.ns.fetchUnseenCount().subscribe();
    //this.ns.initSse();  
      this.ns.ensureConnected();                // s’assure que le flux est ouvert
  }
  mark(id: string){ this.ns.markSeen(id).subscribe(); }
  markAll(){ this.ns.markAllSeen().subscribe(); }
}
