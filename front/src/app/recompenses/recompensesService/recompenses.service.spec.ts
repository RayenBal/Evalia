import { TestBed } from '@angular/core/testing';

import { RecompensesService } from './recompenses.service';

describe('RecompensesService', () => {
  let service: RecompensesService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RecompensesService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
