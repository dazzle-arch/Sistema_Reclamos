import { TestBed } from '@angular/core/testing';
import { NotificacionEstado } from './notificacion-estado';

describe('NotificacionEstado', () => {
  let service: NotificacionEstado;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(NotificacionEstado);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
