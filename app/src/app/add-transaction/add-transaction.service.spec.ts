import { TestBed } from '@angular/core/testing';

import { AddTransactionService } from './add-transaction.service';

describe('AddTransactionService', () => {
  let service: AddTransactionService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AddTransactionService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
