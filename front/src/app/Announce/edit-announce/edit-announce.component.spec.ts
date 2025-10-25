import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditAnnounceComponent } from './edit-announce.component';

describe('EditAnnounceComponent', () => {
  let component: EditAnnounceComponent;
  let fixture: ComponentFixture<EditAnnounceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditAnnounceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditAnnounceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
