// src/app/modules/calendar/calendar.component.ts
import { Component, ViewChild, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { FullCalendarModule } from '@fullcalendar/angular';
import { catchError, map, switchMap } from 'rxjs/operators';


import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { MatDialogModule } from '@angular/material/dialog';
import {
  CalendarOptions, DateSelectArg, EventClickArg, EventInput,
} from '@fullcalendar/core';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import timeGridPlugin from '@fullcalendar/timegrid';
import listPlugin from '@fullcalendar/list';
import { MatDialog } from '@angular/material/dialog';
import { Planning, AppointmentStatus, fullName } from './planning/planningModel/planning.model';
import { PlanningService } from './planning/planning.service';
import { PlanDetailComponent } from './dialogs/plan-detail/plan-detail/plan-detail.component';
import { FormDialogComponent } from './dialogs/form-dialog/form-dialog/form-dialog.component';
import { AuthService } from '../auth/auth-service/auth.service';
import { AnnounceServiceService } from '../Announce/AnnounceService/announce-service.service';
import { forkJoin, of } from 'rxjs';
@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.scss'],
  standalone: true,
  imports: [   CommonModule,

    // FullCalendar
    FullCalendarModule,

    // Angular Material utilisés dans le template
    MatButtonModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatSelectModule,
    MatOptionModule,
    MatSnackBarModule,
    MatDialogModule,]
})
export class CalendarComponent implements OnInit {
   @ViewChild('planning', { static: false })
  planning: Planning = new Planning();
  exampleDatabase?: PlanningService;
  dialogTitle: string;
  filterOptions = 'All';
  planningData!: Planning;
  plannings: Planning[]=[]
  filterItems: string[] = [
  ];

  calendarEvents?: EventInput[];
  tempEvents?: EventInput[];
  listlocations : any;
  listcategories : any;
  locationid : number = 0;
  categoryid : number= 0;
  public filters = [
   
  ];
  // Filtrage optionnel par statut
  activeStatuses: Set<AppointmentStatus> = new Set<AppointmentStatus>([
    'PENDING', 'CONFIRMED', 'CANCELLED', 'COMPLETED'
  ]);

  constructor(
    private api: PlanningService,
    private dialog: MatDialog,public auth:AuthService,private annService : AnnounceServiceService
  ) {
     this.dialogTitle = 'Add New Planning';
    const blankObject = {} as Planning;
    this.planning = new Planning(blankObject);
  }

  ngOnInit(): void {
    console.log("this.auth.userId ",this.auth.userId);
     if (this.auth.userType === 'Paneliste') {
    this.loadMine();
  } else {
    this.loadOwner();
  }
   // this.loadOwner();
  }

  // Charge calendrier de l’annonceur connecté
loadOwner() {
  this.api.listOwner(this.auth.userId ?? 0).pipe(
    switchMap(appts => {
      this.plannings = appts ?? [];

      // Construire la liste des requêtes à faire pour hydrater chaque planning.
      const hydrateCalls = this.plannings.map(p => 
        forkJoin({
          ann: this.annService.getAnnounce((p as any).announcementId).pipe(catchError(() => of(null))),
          pan: this.auth.getUserById((p as any).panelistId).pipe(catchError(() => of(null)))
        }).pipe(map(({ ann, pan }) => {
          if (ann) p.announcement = ann as any;
          if (pan) p.panelist = pan as any;
          return p;
        }))
      );

      // Si aucun RDV, on renvoie juste un of([])
      return hydrateCalls.length ? forkJoin(hydrateCalls) : of([]);
    })
  ).subscribe({
    next: () => this.pushToCalendar(), // faire le mapping events APRÈS hydratation
    error: (e) => console.error('Erreur chargement owner', e)
  });
}
  calendarOptions: CalendarOptions = {
    plugins: [dayGridPlugin, timeGridPlugin, listPlugin, interactionPlugin],
    headerToolbar: {
      left: 'prev,next today',
      center: 'title',
      right: 'dayGridMonth,timeGridWeek,timeGridDay,listWeek',
    },
    initialView: 'dayGridMonth',
    weekends: true,
    editable: true,
    selectable: true,
    selectMirror: true,
    dayMaxEvents: true,
    select: this.handleDateSelect.bind(this),
    eventClick: this.handleEventClick.bind(this),
  };

  // Charge calendrier du paneliste connecté
loadMine() {
  this.api.listMine(this.auth.userId ?? 0).pipe(
    switchMap(appts => {
      this.plannings = appts ?? [];
      const hydrateCalls = this.plannings.map(p => 
        // Pour un paneliste, l’annonce est la plus utile pour le titre :
        this.annService.getAnnounce((p as any).announcementId).pipe(
          catchError(() => of(null)),
          map(ann => { if (ann) p.announcement = ann as any; return p; })
        )
      );
      return hydrateCalls.length ? forkJoin(hydrateCalls) : of([]);
    })
  ).subscribe({
    next: () => this.pushToCalendar(),
    error: (e) => console.error('Erreur chargement mine', e)
  });
}
  // Transforme en EventInput FullCalendar
  private pushToCalendar() {
    const events: EventInput[] = this.plannings
      //.filter(a => this.activeStatuses.has(a.status))
      .map(a => ({
        id: a.id,
        title: this.buildTitle(a),
        start: a.startsAt,
        end: a.endsAt,
        className: this.classFor(a.status),
        extendedProps: {
          status: a.status,
          announcementName: a.announcement?.announceName,
          owner: fullName(a.owner),
          panelist: fullName(a.panelist || undefined)
        }
      }));
    this.calendarOptions = { ...this.calendarOptions, events };
  }
private buildTitle(a: Planning) {
  return `${a.announcement?.announceName || 'Annonce'} · ${a.panelist ? fullName(a.panelist) : 'Créneau libre'}`;
}
 /* private buildTitle(a: Planning): string {
    const ann = a.announcement?.announceName || 'Annonce';
    const who = a.panelist ? fullName(a.panelist) : 'Créneau libre';
    return `${ann} · ${who}`;
  }*/

  private classFor(st: AppointmentStatus): string {
    switch (st) {
      case 'PENDING':   return 'fc-event-warning';
      case 'CONFIRMED': return 'fc-event-success';
      case 'CANCELLED':  return 'fc-event-danger';
      case 'COMPLETED': return 'fc-event-primary';
      //case 'NO_SHOW':   return 'fc-event-info';
      default:          return 'fc-event-info';
    }
  }
 private lastSelectedDay?: Date;

  /** yyyy-MM-dd depuis une Date locale */
  private ymd(d: Date): string {
    return `${d.getFullYear()}-${String(d.getMonth() + 1).padStart(2, '0')}-${String(d.getDate()).padStart(2, '0')}`;
  }

  /** Convertit une ISO (venant du backend) en Date locale */
  private parseIsoLocal(s: string): Date {
    // si ton backend renvoie '2025-10-17T10:00:00', Date(s) convient
    // si c'est '2025-10-17 10:00:00' (sans 'T'), remplace l'espace par 'T'
    return new Date(s.includes('T') ? s : s.replace(' ', 'T'));
  }
  // Ouvre la liste des RDV du jour sélectionné
  handleDateSelect(selectInfo: DateSelectArg) {
  const start = selectInfo.start;              // début du jour (local)
    const end   = selectInfo.end ?? selectInfo.start; // fin exclusive

    this.lastSelectedDay = start;

    const isInRange = (iso: string) => {
      const d = this.parseIsoLocal(iso);
      return d >= start && d < end;             // même jour
    };

    const sameDay = this.plannings.filter(a => isInRange(a.startsAt));
  this.dialog.open(PlanDetailComponent, { data: { appointments: sameDay } });

    /*this.dialog.open(PlanDetailComponent, {
      data: { appointments: sameDay }
    });
    this.dialog.open(PlanDetailComponent, {
    data: { appointments: sameDay, isPaneliste: true }   });

this.dialog.open(PlanDetailComponent, {
    data: {
      appointments: sameDay,
      isPaneliste:  this.auth.userType === 'Paneliste',
      isAnnonceur:  this.auth.userType === 'Announceur' // ou 'Advertiser' selon ton enum
    }
  });*/
  
  }

  // Ouvre un RDV précis (édition/assignation dans le dialog)
  handleEventClick(arg: EventClickArg) {
    const appt = this.plannings.find(a => a.id === arg.event.id);
    if (!appt) return;
    this.dialog.open(FormDialogComponent, { data: { appointment: appt } })
      .afterClosed().subscribe(changed => {
       // if (changed) this.loadOwner(); // ou this.loadMine() selon 
         if (!changed) return;
  if (this.auth.userType === 'Paneliste') this.loadMine();
  else this.loadOwner();
  });
  }

  // UI — boutons simples
  showOwner() { this.loadOwner(); }
  showMine()  { this.loadMine();  }

  toggleStatus(s: AppointmentStatus) {
    if (this.activeStatuses.has(s)) this.activeStatuses.delete(s);
    else this.activeStatuses.add(s);
    this.pushToCalendar();
  }

  addSlot() {
    this.dialog.open(FormDialogComponent, { data: { createMode: true } })
      .afterClosed().subscribe(ok => { if (ok) this.loadOwner(); });
  }
  addNewEvent(){
    const dialogRef = this.dialog.open(FormDialogComponent, {
      data: {
        planning: this.planning,
        action: 'add',
      }
    });
  }

}
