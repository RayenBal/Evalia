import { TestBed } from '@angular/core/testing';

import { OwnerRewardsService } from './owner-rewards.service';

describe('OwnerRewardsService', () => {
  let service: OwnerRewardsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(OwnerRewardsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
