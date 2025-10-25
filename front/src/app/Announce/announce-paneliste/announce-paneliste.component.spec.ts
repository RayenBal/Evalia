import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AnnouncePanelisteComponent } from './announce-paneliste.component';

describe('AnnouncePanelisteComponent', () => {
  let component: AnnouncePanelisteComponent;
  let fixture: ComponentFixture<AnnouncePanelisteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AnnouncePanelisteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(AnnouncePanelisteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
