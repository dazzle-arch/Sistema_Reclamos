import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ConsultarEstado } from './consultar-estado';

describe('ConsultarEstado', () => {
  let component: ConsultarEstado;
  let fixture: ComponentFixture<ConsultarEstado>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ConsultarEstado],
    }).compileComponents();

    fixture = TestBed.createComponent(ConsultarEstado);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
