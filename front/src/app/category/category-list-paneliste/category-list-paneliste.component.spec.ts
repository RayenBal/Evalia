import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CategoryListPanelisteComponent } from './category-list-paneliste.component';

describe('CategoryListPanelisteComponent', () => {
  let component: CategoryListPanelisteComponent;
  let fixture: ComponentFixture<CategoryListPanelisteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CategoryListPanelisteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(CategoryListPanelisteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
