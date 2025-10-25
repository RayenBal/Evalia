import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnerRewardsComponent } from './owner-rewards.component';

describe('OwnerRewardsComponent', () => {
  let component: OwnerRewardsComponent;
  let fixture: ComponentFixture<OwnerRewardsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OwnerRewardsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OwnerRewardsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
