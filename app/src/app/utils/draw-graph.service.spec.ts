import { TestBed } from '@angular/core/testing';

import { DrawGraphService } from './draw-graph.service';

describe('DrawGraphService', () => {
  let service: DrawGraphService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DrawGraphService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
