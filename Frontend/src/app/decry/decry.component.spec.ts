import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { DecryComponent } from './decry.component';

describe('DecryComponent', () => {
  let component: DecryComponent;
  let fixture: ComponentFixture<DecryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ DecryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DecryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
