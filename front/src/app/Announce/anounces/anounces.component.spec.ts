import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnouncesComponent } from './anounces.component';

describe('AnouncesComponent', () => {
  let component: AnouncesComponent;
  let fixture: ComponentFixture<AnouncesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnouncesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AnouncesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
