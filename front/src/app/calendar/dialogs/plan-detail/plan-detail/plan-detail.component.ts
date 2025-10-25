// src/app/modules/calendar/dialogs/plan-detail/plan-detail.component.ts
import { Component, Inject } from '@angular/core';
import { Planning,fullName } from '../../../planning/planningModel/planning.model';


import { CommonModule } from '@angular/common';
import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';

import { MatTableModule } from '@angular/material/table';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { ReactiveFormsModule, FormArray, FormBuilder, FormGroup,FormControl } from '@angular/forms';
import { PlanningService } from '../../../planning/planning.service';
//import { Planning } from '../../../planning/planningModel/planning.model';
//import { fullName } from '../../../planning/planningModel/planning.model';
import { AppointmentStatus } from '../../../planning/planningModel/planning.model';
import { AuthService } from '../../../../auth/auth-service/auth.service';
type DataIn = {
  appointments: Planning[];
  isAnnonceur?: boolean;
  isPaneliste?: boolean;
};
@Component({
  selector: 'app-plan-detail',
  imports: [
    CommonModule,

    // Material nécessaires au template
    MatDialogModule,
    MatTableModule,
    MatButtonModule,
    MatIconModule,
    ReactiveFormsModule,
  ],
  templateUrl: './plan-detail.component.html',
  styleUrls: ['./plan-detail.component.scss'],
  standalone: true,
})
export class PlanDetailComponent {
  isAnnonceur = !!this.data?.isAnnonceur;
 isPaneliste = !!this.data?.isPaneliste;
 //   get isAnnonceur() { return this.auth.userType === 'Announceur'; }
 // get isPaneliste() { return this.auth.userType === 'Paneliste'; }
 rows: Planning[] = this.data.appointments ?? [];

  // contrôles de statut (un par rendez-vous) — utilisés côté paneliste
  statusCtrl: Record<string, FormControl<AppointmentStatus>> = {};

  // options enum (attention : "CANCELLED" avec 2 L)
  statusOptions: AppointmentStatus[] = ['PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED'];
  //rows!: FormArray<FormGroup>;
  displayedColumns = ['annonce', 'paneliste', 'debut', 'fin', 'statut', 'actions'];
  /*constructor(
    public dialogRef: MatDialogRef<PlanDetailComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { appointments: Planning[] },
    
  ) {}

  name(u: any) { return fullName(u); }*/

    constructor(
    public dialogRef: MatDialogRef<PlanDetailComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DataIn,
    //@Inject(MAT_DIALOG_DATA)
    //public data: { appointments: Planning[]; isAnnonceur?: boolean; isPaneliste?: boolean },
   private auth:AuthService,
    private api: PlanningService
  ) {
    if (this.isPaneliste) {
      for (const a of this.rows) {
        this.statusCtrl[a.id] = new FormControl<AppointmentStatus>(a.status, { nonNullable: true });
      }
    }
  }

  name(u: any) {
    return fullName(u);
  }
canEdit(a: Planning) {
    return !!a.panelist && a.panelist.id_user === (this.auth.userId ?? -1);
  }

  changeStatus(a: Planning, newStatus: AppointmentStatus) {
    if (!this.canEdit(a)) return;
    if (a.status === newStatus) return;
    this.api.updateStatus(a.id, { status: newStatus }, this.auth.userId!)
      .subscribe(updated => a.status = updated.status);
  }


  
}
 /*onStatusChange(appt: Planning) {
    if (!this.isPaneliste) return;
    const ctrl = this.statusCtrl[appt.id];
    if (!ctrl || !ctrl.value) return;

    const newStatus = ctrl.value;
    // PATCH /plannings/{id}/status
    this.api.updateStatus(appt.id, { status: newStatus }).subscribe({
      next: () => {
        appt.status = newStatus; // maj locale
      },
      error: (err) => {
        // rollback visuel si besoin
        ctrl.setValue(appt.status, { emitEvent: false });
        console.error('Erreur de mise à jour du statut', err);
      },
    });
  }*/

