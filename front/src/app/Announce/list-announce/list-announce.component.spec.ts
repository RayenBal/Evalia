import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ListAnnounceComponent } from './list-announce.component';

describe('ListAnnounceComponent', () => {
  let component: ListAnnounceComponent;
  let fixture: ComponentFixture<ListAnnounceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ListAnnounceComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ListAnnounceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
