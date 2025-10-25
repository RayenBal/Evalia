import { TestBed } from '@angular/core/testing';

import { FeedbackOwnerService } from './feedback-owner.service';

describe('FeedbackOwnerService', () => {
  let service: FeedbackOwnerService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FeedbackOwnerService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
