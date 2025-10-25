import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsAnnounceComponent } from './details-announce.component';

describe('DetailsAnnounceComponent', () => {
  let component: DetailsAnnounceComponent;
  let fixture: ComponentFixture<DetailsAnnounceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetailsAnnounceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DetailsAnnounceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
