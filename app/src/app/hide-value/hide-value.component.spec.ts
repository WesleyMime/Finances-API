import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HideValueComponent } from './hide-value.component';

describe('HideValueComponent', () => {
  let component: HideValueComponent;
  let fixture: ComponentFixture<HideValueComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [HideValueComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(HideValueComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
