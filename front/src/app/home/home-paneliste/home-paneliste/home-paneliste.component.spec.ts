import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomePanelisteComponent } from './home-paneliste.component';

describe('HomePanelisteComponent', () => {
  let component: HomePanelisteComponent;
  let fixture: ComponentFixture<HomePanelisteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomePanelisteComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HomePanelisteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
