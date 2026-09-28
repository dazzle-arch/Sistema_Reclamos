import { ComponentFixture, TestBed } from '@angular/core/testing';
import { BandejaCasos } from './bandeja-casos';

describe('BandejaCasos', () => {
  let component: BandejaCasos;
  let fixture: ComponentFixture<BandejaCasos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BandejaCasos],
    }).compileComponents();

    fixture = TestBed.createComponent(BandejaCasos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
