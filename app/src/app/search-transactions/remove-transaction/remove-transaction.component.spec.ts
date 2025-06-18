import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RemoveTransactionComponent } from './remove-transaction.component';

describe('RemoveTransactionComponent', () => {
  let component: RemoveTransactionComponent;
  let fixture: ComponentFixture<RemoveTransactionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RemoveTransactionComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RemoveTransactionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
