import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UpdateAnnounceComponent } from './update-announce.component';

describe('UpdateAnnounceComponent', () => {
  let component: UpdateAnnounceComponent;
  let fixture: ComponentFixture<UpdateAnnounceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UpdateAnnounceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(UpdateAnnounceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
