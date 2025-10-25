import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReclamationService } from '../reclamationService/reclamation.service';
import { Motif, TypeReclamation, reclamation } from '../reclamationModel/reclamation';
import { AuthService } from '../../auth/auth-service/auth.service';
import { AnnounceServiceService } from '../../Announce/AnnounceService/announce-service.service';
import { announce } from '../../Announce/AnnounceModel/announce';
@Component({
  selector: 'app-reclamation-add',
  standalone: true,
 imports: [ReactiveFormsModule, CommonModule, RouterModule],
   templateUrl: './reclamation-add.component.html',
  styleUrl: './reclamation-add.component.css'
})
export class ReclamationAddComponent implements OnInit{
  private baseUrl = 'http://localhost:8081/reclamation';
    reclamationForm!: FormGroup;
  motifOptions = Object.values(Motif);
 // typeReclamationOptions = Object.values(TypeReclamation);
  isEditMode = false;
  reclamationId: string = '';
  get isAnnonceur() { return this.auth.userType === 'Announceur'; }
  get isPaneliste() { return this.auth.userType === 'Paneliste'; }
  constructor(
    private fb: FormBuilder,
    private reclamationService: ReclamationService,
    private router: Router,
    private route: ActivatedRoute,
    public auth:AuthService,
     private announceSvc: AnnounceServiceService
  ) {}
 logout() {
    this.auth.clearToken();
    this.router.navigate(['/login']);
  }
  selectedId?: string;
  ngOnInit(): void {
    this.reclamationForm = this.fb.group({
      content: ['', Validators.required],
      //typeReclamation: ['', Validators.required],
      motif: ['']
    });

    // Vérifier s'il s'agit d'une modification
this.route.paramMap.subscribe(pm => {
    const id = pm.get('id');
    if (id) {
      this.isEditMode = true;
      this.reclamationId = id;
      this.loadReclamation(id);
    }
    });
    this.announceSvc.getMyAnnounces().subscribe(list => {
    this.selectedId = list?.[0]?.idAnnouncement ?? undefined;
  })
    
  }

  loadReclamation(id: string): void {
    this.reclamationService.getOne(id).subscribe({
      next: (reclamation) => {
        this.reclamationForm.patchValue({
          content: reclamation.content,
          //typeReclamation: reclamation.typeReclamation,
          motif: reclamation.motif
        });
      },
      error: (err) => {
        console.error('⛔ Erreur lors du chargement de la réclamation', err);
      }
    });
  }

  onSubmit(): void {
    if (this.reclamationForm.invalid) {
      console.warn("⛔ Formulaire invalide", this.reclamationForm.errors);
      return;
    }

    const formData: reclamation = this.reclamationForm.value;

    const request = this.isEditMode
      ? this.reclamationService.updateReclamation(this.reclamationId, formData)
      : this.reclamationService.createReclamation(formData);

    request.subscribe({
      next: () => {
        const action = this.isEditMode ? 'modifiée' : 'ajoutée';
        console.log(`✅ Réclamation ${action} avec succès`);
        this.router.navigate(['/reclamations']);
      },
      error: (err) => console.error("⛔ Erreur", err)
    });
  }
setSelected(a: announce) {
  this.selectedId = a.idAnnouncement;
}
  goBack() {
    this.router.navigate(['/reclamations']);
  }
}


