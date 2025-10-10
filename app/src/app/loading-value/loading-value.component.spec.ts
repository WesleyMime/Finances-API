import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadingValueComponent } from './loading-value.component';

describe('LoadingValueComponent', () => {
  let component: LoadingValueComponent;
  let fixture: ComponentFixture<LoadingValueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoadingValueComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(LoadingValueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
