import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DetalleReclamo } from './detalle-reclamo';

describe('DetalleReclamo', () => {
  let component: DetalleReclamo;
  let fixture: ComponentFixture<DetalleReclamo>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalleReclamo],
    }).compileComponents();

    fixture = TestBed.createComponent(DetalleReclamo);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
