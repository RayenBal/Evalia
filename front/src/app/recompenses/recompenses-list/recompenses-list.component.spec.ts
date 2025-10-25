import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RecompensesListComponent } from './recompenses-list.component';

describe('RecompensesListComponent', () => {
  let component: RecompensesListComponent;
  let fixture: ComponentFixture<RecompensesListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RecompensesListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(RecompensesListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
