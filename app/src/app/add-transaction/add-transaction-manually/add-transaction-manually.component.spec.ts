import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddTransactionManuallyComponent } from './add-transaction-manually.component';

describe('AddTransactionComponent', () => {
  let component: AddTransactionManuallyComponent;
  let fixture: ComponentFixture<AddTransactionManuallyComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddTransactionManuallyComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(AddTransactionManuallyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
