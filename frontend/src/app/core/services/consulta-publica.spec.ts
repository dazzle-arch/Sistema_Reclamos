import { TestBed } from '@angular/core/testing';
import { ConsultaPublica } from './consulta-publica';

describe('ConsultaPublica', () => {
  let service: ConsultaPublica;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConsultaPublica);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
