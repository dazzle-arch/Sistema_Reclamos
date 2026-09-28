import { ComponentFixture, TestBed } from '@angular/core/testing';
import { SidebarFuncionario } from './sidebar-funcionario';

describe('SidebarFuncionario', () => {
  let component: SidebarFuncionario;
  let fixture: ComponentFixture<SidebarFuncionario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SidebarFuncionario],
    }).compileComponents();

    fixture = TestBed.createComponent(SidebarFuncionario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
