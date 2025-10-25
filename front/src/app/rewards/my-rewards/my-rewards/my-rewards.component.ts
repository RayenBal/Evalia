import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RewardsService, PanelistRewardItem } from '../../rewards.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../auth/auth-service/auth.service';
@Component({
  selector: 'app-my-rewards',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './my-rewards.component.html'
})
export class MyRewardsComponent implements OnInit {
  items: PanelistRewardItem[] = [];
  totals: Record<string, string> = {};
  get totalKeys() { return Object.keys(this.totals); }

  constructor(private rewards: RewardsService,private router: Router, public auth:AuthService) {}
  ngOnInit(): void {
    this.rewards.getMine().subscribe(dto => {
      this.items = dto.items;
      this.totals = dto.totals as any;
    });
  }
  logout() {
    this.auth.clearToken();
    this.router.navigate(['/login']);
  }
}
