import { ComponentFixture, TestBed } from '@angular/core/testing';
import { DerivarCaso } from './derivar-caso';

describe('DerivarCaso', () => {
  let component: DerivarCaso;
  let fixture: ComponentFixture<DerivarCaso>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DerivarCaso],
    }).compileComponents();

    fixture = TestBed.createComponent(DerivarCaso);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
