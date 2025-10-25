import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecompensesAddComponent } from './recompenses-add.component';

describe('RecompensesAddComponent', () => {
  let component: RecompensesAddComponent;
  let fixture: ComponentFixture<RecompensesAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecompensesAddComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RecompensesAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
