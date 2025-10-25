import { ComponentFixture, TestBed } from '@angular/core/testing';

import { OwnerFeedbackListComponent } from './owner-feedback-list.component';

describe('OwnerFeedbackListComponent', () => {
  let component: OwnerFeedbackListComponent;
  let fixture: ComponentFixture<OwnerFeedbackListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OwnerFeedbackListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(OwnerFeedbackListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
