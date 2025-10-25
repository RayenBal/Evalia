import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditRecompensesComponent } from './edit-recompenses.component';

describe('EditRecompensesComponent', () => {
  let component: EditRecompensesComponent;
  let fixture: ComponentFixture<EditRecompensesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EditRecompensesComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(EditRecompensesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
