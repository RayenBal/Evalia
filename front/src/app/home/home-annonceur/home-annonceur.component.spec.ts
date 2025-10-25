import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeAnnonceurComponent } from './home-annonceur.component';

describe('HomeAnnonceurComponent', () => {
  let component: HomeAnnonceurComponent;
  let fixture: ComponentFixture<HomeAnnonceurComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HomeAnnonceurComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(HomeAnnonceurComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
