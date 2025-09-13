import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AddTransactionByAiComponent } from './add-transaction-by-ai.component';

describe('AddTransactionByAiComponent', () => {
  let component: AddTransactionByAiComponent;
  let fixture: ComponentFixture<AddTransactionByAiComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AddTransactionByAiComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AddTransactionByAiComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
