import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CasosAtendidos } from './casos-atendidos';

describe('CasosAtendidos', () => {
  let component: CasosAtendidos;
  let fixture: ComponentFixture<CasosAtendidos>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CasosAtendidos],
    }).compileComponents();

    fixture = TestBed.createComponent(CasosAtendidos);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
