import { Component, OnInit } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ReclamationService } from '../reclamationService/reclamation.service';
import { Motif, reclamation } from '../reclamationModel/reclamation';
import { AuthService } from '../../auth/auth-service/auth.service';
import { announce } from '../../Announce/AnnounceModel/announce';
import { AnnounceServiceService } from '../../Announce/AnnounceService/announce-service.service';
@Component({
  selector: 'app-reclamation-list',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule,RouterModule],
  templateUrl: './reclamation-list.component.html',
  styleUrl: './reclamation-list.component.css'
})
export class ReclamationListComponent implements OnInit {
  items: reclamation[] = [];
  loading = false;
  error = '';
  get isAnnonceur() { return this.auth.userType === 'Announceur'; }
  get isPaneliste() { return this.auth.userType === 'Paneliste'; }
  constructor(private svc: ReclamationService, private router: Router, public auth:AuthService, private announceSvc: AnnounceServiceService) {}
selectedId?: string;
  //ngOnInit(): void { this.refresh(); }
 /*ngOnInit(): void {
    this.svc.listMine().subscribe(rows => this.items = rows);
  }*/
 ngOnInit(): void {
  this.svc.listMine().subscribe(rows => this.items = rows);

  // >>> détermine un id par défaut pour le lien "Récompenses"
  this.announceSvc.getMyAnnounces().subscribe(list => {
    this.selectedId = list?.[0]?.idAnnouncement ?? undefined;
  });}
  refresh() {
    this.loading = true; this.error = '';
    this.svc.listMine().subscribe({
      next: d => { this.items = d ?? []; this.loading = false; },
      error: e => { this.error = e.message || 'Erreur de chargement'; this.loading = false; }
    });
  }

  add()  { this.router.navigate(['/reclamations/new']); }
  edit(id?: string) { if (id) this.router.navigate(['/reclamations', id, 'edit']); }
  del(id?: string) {
    if (!id) return;
    if (!confirm('Supprimer cette réclamation ?')) return;
    this.svc.delete(id).subscribe({ next: () => this.refresh() });
  }
setSelected(a: announce) {
  this.selectedId = a.idAnnouncement;
}
   logout() {
    this.auth.clearToken();
    this.router.navigate(['/login']);
  }
}














  
  /*motifOptions = Object.values(Motif);
  isEditMode = false;
  reclamationId = '';

  reclamationForm = this.fb.group({
    content: ['', Validators.required],
    motif: ['', Validators.required]   // ← required pour forcer le choix
  });

  constructor(
    private fb: FormBuilder,
    private svc: ReclamationService,
    private router: Router,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.isEditMode = true;
      this.reclamationId = id;
      this.svc.getOne(id).subscribe({
        next: r => this.reclamationForm.patchValue({ content: r.content ?? '', motif: r.motif ?? '' }),
        error: e => console.error('⛔ Chargement réclamation', e)
      });
    }
  }

  onSubmit(): void {
    if (this.reclamationForm.invalid) return;

    const body: reclamation = this.reclamationForm.value as reclamation;
    const req = this.isEditMode
      ? this.svc.updateReclamation(this.reclamationId, body)
      : this.svc.createReclamation(body);

    req.subscribe({
      next: () => this.router.navigate(['/reclamations']),
      error: e => console.error('⛔ Erreur submit', e)
    });
  }

  goBack() { this.router.navigate(['/reclamations']); }
}*/