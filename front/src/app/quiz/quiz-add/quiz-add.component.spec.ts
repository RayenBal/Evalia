import { ComponentFixture, TestBed } from '@angular/core/testing';

import { QuizAddComponent } from './quiz-add.component';

describe('QuizAddComponent', () => {
  let component: QuizAddComponent;
  let fixture: ComponentFixture<QuizAddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [QuizAddComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(QuizAddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
