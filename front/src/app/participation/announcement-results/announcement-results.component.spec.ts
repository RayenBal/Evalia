import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnnouncementResultsComponent } from './announcement-results.component';

describe('AnnouncementResultsComponent', () => {
  let component: AnnouncementResultsComponent;
  let fixture: ComponentFixture<AnnouncementResultsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnnouncementResultsComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AnnouncementResultsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
