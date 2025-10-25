import { TestBed } from '@angular/core/testing';

import { ResponsePanelisteService } from './response-paneliste.service';

describe('ResponsePanelisteService', () => {
  let service: ResponsePanelisteService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ResponsePanelisteService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
