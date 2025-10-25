// src/app/modules/calendar/dialogs/form-dialog/form-dialog.component.ts
import { Component, Inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { MAT_DIALOG_DATA, MatDialogModule, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';





import {
  Planning, AppointmentStatus, fullName,
  SlimUser
} from '../../../planning/planningModel/planning.model';
import {
  PlanningService, SlotCreateDto, AssignPanelistDto, UpdateStatusDto
} from '../../../planning/planning.service';

import { FormBuilder, Validators } from '@angular/forms';
import { AnnounceServiceService } from '../../../../Announce/AnnounceService/announce-service.service';
import { announce } from '../../../../Announce/AnnounceModel/announce';
import { AuthService } from '../../../../auth/auth-service/auth.service';

@Component({
  selector: 'app-form-dialog',
    imports: [
    CommonModule,
    FormsModule,
    ReactiveFormsModule,

    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatOptionModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonModule,
    MatIconModule,
  ],
  templateUrl: './form-dialog.component.html',
  styleUrls: ['./form-dialog.component.scss'],
  standalone: true
})
export class FormDialogComponent {
  // createMode: création d’un unique créneau (owner)
  // appointment: édition (assignation/status)
  createMode = !!this.data?.createMode;
  appointment?: Planning;

  // TODO: branche ces listes à tes vrais endpoints si besoin
  // (sélecteur d’annonce et de paneliste)
  announces: announce[] = [];
  panelists: SlimUser[] = [];

  createForm = this.fb.group({
    announcementId: ['', Validators.required],
    startsAt: ['', Validators.required],
    endsAt: ['', Validators.required],
    panelistId: [null, Validators.required],

  });


  statusForm = this.fb.group({
    status: ['PENDING' as AppointmentStatus, Validators.required],
  });

  constructor(
    private fb: FormBuilder,
    private api: PlanningService,private announceService:AnnounceServiceService,public auth:AuthService,private panelistservice : AuthService,
    public dialogRef: MatDialogRef<FormDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: { createMode?: boolean; appointment?: Planning }
  ) {
    this.appointment = data?.appointment;
   announceService.getMyAnnounces().subscribe(x => {
 this.announces=x
    })
auth.getPaneList().subscribe(x => {
 this.panelists=x
    })
      if (this.appointment) {
      this.statusForm.patchValue({ status: this.appointment.status });
    }
  }

  name(u: any) { return fullName(u); }

  // Owner: créer un créneau (1 slot)
  createSlot() {
    if (this.createForm.invalid) return;
    const v = this.createForm.value;
    const dto: SlotCreateDto = {
      announcementId: v.announcementId!,
      slots: [{
        startsAt: new Date(v.startsAt!).toISOString(),
        endsAt:   new Date(v.endsAt!).toISOString(),
        panelistId : v.panelistId!
      }]
      
    };
    console.log("dto ",dto);
    
    this.api.createSlots(this.auth.userId ?? 0,dto).subscribe(() => this.dialogRef.close(true));
  }
 get isAnnonceur() { return this.auth.userType === 'Announceur'; }
  get isPaneliste() { return this.auth.userType === 'Paneliste'; }

  /** Le paneliste peut-il éditer ce RDV ? (doit être l’assigné) */
get canPanelistEdit(): boolean {
    if (!this.isPaneliste || !this.appointment) return false;
    // >>> utilise panelistId (pas panelist)
    return (this.appointment.panelistId ?? null) === (this.auth.userId ?? null);
  }

  /** Statuts autorisés pour le paneliste */
  get panelistOptions(): AppointmentStatus[] {
    return ['CONFIRMED', 'CANCELLED'];
  }
  changeStatus() {
    if (!this.appointment || this.statusForm.invalid) return;
    if (!this.canPanelistEdit) return; // garde-fou UI

    const newStatus = this.statusForm.value.status as AppointmentStatus;
    if (!this.panelistOptions.includes(newStatus)) return; // filtrage côté client

    const body: UpdateStatusDto = { status: newStatus };

    this.api.updateStatus(this.appointment.id, body, this.auth.userId!)
      .subscribe({
        next: () => this.dialogRef.close(true),
        error: (e) => console.error('PATCH /status refusé', e)
      });
  }
}
