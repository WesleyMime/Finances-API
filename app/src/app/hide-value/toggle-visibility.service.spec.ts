import { TestBed } from '@angular/core/testing';

import { ToggleVisibilityService } from './toggle-visibility.service';

describe('ToggleVisibilityService', () => {
  let service: ToggleVisibilityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ToggleVisibilityService);
  });

  it('should be created', () => {
    expect(service.).toBeTruthy();
  });
});
