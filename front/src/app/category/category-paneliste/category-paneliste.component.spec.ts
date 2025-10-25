import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryPanelisteComponent } from './category-paneliste.component';

describe('CategoryPanelisteComponent', () => {
  let component: CategoryPanelisteComponent;
  let fixture: ComponentFixture<CategoryPanelisteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryPanelisteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CategoryPanelisteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
