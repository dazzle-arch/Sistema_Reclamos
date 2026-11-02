import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CampanaNotificaciones } from './campana-notificaciones';

describe('CampanaNotificaciones', () => {
  let component: CampanaNotificaciones;
  let fixture: ComponentFixture<CampanaNotificaciones>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CampanaNotificaciones],
    }).compileComponents();

    fixture = TestBed.createComponent(CampanaNotificaciones);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
