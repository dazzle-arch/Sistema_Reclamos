import { ComponentFixture, TestBed } from '@angular/core/testing';
import { AtenderCaso } from './atender-caso';

describe('AtenderCaso', () => {
  let component: AtenderCaso;
  let fixture: ComponentFixture<AtenderCaso>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AtenderCaso],
    }).compileComponents();

    fixture = TestBed.createComponent(AtenderCaso);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
