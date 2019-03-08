import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EncryComponent } from './encry.component';

describe('EncryComponent', () => {
  let component: EncryComponent;
  let fixture: ComponentFixture<EncryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EncryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EncryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
