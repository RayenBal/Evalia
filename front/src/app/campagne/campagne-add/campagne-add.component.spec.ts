import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CampagneAddComponent } from './campagne-add.component';

describe('CampagneAddComponent', () => {
  let component: CampagneAddComponent;
  let fixture: ComponentFixture<CampagneAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CampagneAddComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CampagneAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
