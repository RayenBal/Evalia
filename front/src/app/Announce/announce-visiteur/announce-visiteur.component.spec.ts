import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnnounceVisiteurComponent } from './announce-visiteur.component';

describe('AnnounceVisiteurComponent', () => {
  let component: AnnounceVisiteurComponent;
  let fixture: ComponentFixture<AnnounceVisiteurComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnnounceVisiteurComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AnnounceVisiteurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
