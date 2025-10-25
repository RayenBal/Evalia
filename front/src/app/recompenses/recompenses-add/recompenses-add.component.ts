
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { RecompensesService } from '../recompensesService/recompenses.service';
import { TypeRecompenses, recompenses } from '../recompensesModel/recompenses';
@Component({
  selector: 'app-recompenses-add',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  templateUrl: './recompenses-add.component.html',
  styleUrl: './recompenses-add.component.css'
})
export class RecompensesAddComponent {

    recompensesForm!: FormGroup;
  typeOptions = Object.values(TypeRecompenses);
  isEditMode = false;
  recompenseId: string = '';

  constructor(
    private fb: FormBuilder,
    private recompensesService: RecompensesService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.recompensesForm = this.fb.group({
      typeRecompenses: ['', Validators.required]
    });

    this.route.params.subscribe(params => {
      if (params['id']) {
        this.isEditMode = true;
        this.recompenseId = params['id'];
        this.loadRecompense(this.recompenseId);
      }
    });
  }

  loadRecompense(id: string) {
    this.recompensesService.getRecompensesById(id).subscribe({
      next: (res) => {
        this.recompensesForm.patchValue({
          typeRecompenses: res.typeRecompenses
        });
      },
      error: (err) => console.error("Erreur chargement récompense", err)
    });
  }

  onSubmit(): void {
    if (this.recompensesForm.invalid) return;

    const data: recompenses = this.recompensesForm.value;

    const request = this.isEditMode
      ? this.recompensesService.updateRecompenses(this.recompenseId, data)
      : this.recompensesService.createRecompenses(data);

    request.subscribe({
      next: () => {
        console.log("✅ Récompense enregistrée !");
        this.router.navigate(['/recompenses']);
      },
      error: (err) => console.error("⛔ Erreur", err)
    });
  }

  goBack() {
    this.router.navigate(['/recompenses']);
  }
}


