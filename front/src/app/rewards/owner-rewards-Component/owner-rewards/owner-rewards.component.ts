

// src/app/rewards/owner-rewards.component.ts
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RewardStatus } from '../../ownerRewardItem/owner-rewards.service';
import { OwnerRewardItem,OwnerRewardsService } from '../../ownerRewardItem/owner-rewards.service';
import { ActivatedRoute,RouterModule,Router } from '@angular/router';
import { AuthService } from '../../../auth/auth-service/auth.service';
import { AnnounceServiceService } from '../../../Announce/AnnounceService/announce-service.service';
import { announce } from '../../../Announce/AnnounceModel/announce';
@Component({
  selector: 'app-owner-rewards',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './owner-rewards.component.html' 
})
export class OwnerRewardsComponent implements OnInit {
  rows: OwnerRewardItem[] = [];
  filterAnnId = '';
  savingId: string | null = null;
    announces: announce[] = [];

  constructor(private svc: OwnerRewardsService, private route: ActivatedRoute,private router: Router, public auth:AuthService,private announceSvc:AnnounceServiceService ) {}
selectedId?: string;
  /*ngOnInit(): void { this.load(); }*/
   ngOnInit(): void {
    // ⚠️ lit :id si présent, sinon liste globale
    this.route.paramMap.subscribe(pm => {
      const id = pm.get('id');
      this.filterAnnId = id ?? '';
      this.load();
    });
    this.announceSvc.getMyAnnounces().subscribe(list => {
    this.selectedId = list?.[0]?.idAnnouncement ?? undefined;
  });
  }

  /*load() {
    const obs = this.filterAnnId
      ? this.svc.listByAnnouncement(this.filterAnnId)
      : this.svc.listAll();

    obs.subscribe(data => this.rows = data);
  }*/
   load() {
    const id = this.filterAnnId.trim();
    const obs = id ? this.svc.listByAnnouncement(id) : this.svc.listAll();
    obs.subscribe(data => this.rows = data);
  }

  saveStatus(r: OwnerRewardItem) {
    this.savingId = r.earnedRewardId;
    this.svc.updateStatus(r.earnedRewardId, r.status as RewardStatus)
      .subscribe({
        next: () => { this.savingId = null; },
        error: () => { this.savingId = null; alert('Échec de la mise à jour du statut'); }
      });
  }
  setSelected(a: announce) {
    this.selectedId = a.idAnnouncement;
  }
    logout() {
    this.auth.clearToken();
    this.router.navigate(['/login']);
  }

  maskIban(v?: string | null): string {
  if (!v) return 'Non renseigné';
  const s = v.replace(/\s+/g, '');
  if (s.length <= 8) return s;
  return `${s.slice(0,4)} •••• •••• •••• •••• ${s.slice(-4)}`;
}
copy(v?: string | null) {
  if (!v) return;
  navigator.clipboard?.writeText(v).catch(() => {});
}

  
}
