import { Routes } from '@angular/router';
import { SignUpComponent } from './auth/sign-up/sign-up.component';
import { SignInComponent } from './auth/sign-in/sign-in.component';

import { CampagneAddComponent } from './campagne/campagne-add/campagne-add.component';
import { CampagneListComponent } from './campagne/campagne-list/campagne-list.component';
import { CategoryListComponent } from './category/category-list/category-list.component';
import { QuestionAddComponent } from './question/question-add/question-add.component';
import { QuestionListComponent } from './question/question-list/question-list.component';
import { QuizAddComponent } from './quiz/quiz-add/quiz-add.component';
import { QuizListComponent } from './quiz/quiz-list/quiz-list.component';
import { ReclamationAddComponent } from './reclamation/reclamation-add/reclamation-add.component';
import { RecompensesAddComponent } from './recompenses/recompenses-add/recompenses-add.component';
import { RecompensesListComponent } from './recompenses/recompenses-list/recompenses-list.component';
import { AddAnnounceComponent } from './Announce/add-announce/add-announce.component';
import { ListAnnounceComponent } from './Announce/list-announce/list-announce.component';
import { UploadFileComponent } from './Announce/upload-file/upload-file.component';
import { QuizDetailsComponent } from './quiz/quiz-details/quiz-details.component';
import { Component } from '@angular/core';
import { EditAnnounceComponent } from './Announce/edit-announce/edit-announce.component';
import { DetailsAnnounceComponent } from './Announce/details-announce/details-announce.component';
import { HomeComponent } from './home/home/home.component';
import { AnouncesComponent } from './Announce/anounces/anounces.component';
import { UpdateAnnounceComponent } from './Announce/update-announce/update-announce.component';
import { EditRecompensesComponent } from './recompenses/edit-recompenses/edit-recompenses.component';
import { CategoryDetailsComponent } from './category/category-details/category-details.component';
import { FeedbackListComponent } from './feedback/feedback-list/feedback-list.component';
import { VerifyEmailComponent } from './auth/verify-email/verify-email/verify-email.component';
import { ForgotPasswordComponent } from './auth/forgot-password/forgot-password/forgot-password.component';
import { ResetPasswordComponent } from './auth/reset-password/reset-password/reset-password.component';
import { TakeQuizComponent } from './participation/take-quiz/take-quiz.component';
import { AnnouncementResultsComponent } from './participation/announcement-results/announcement-results.component';
import { CategoryPanelisteComponent } from './category/category-paneliste/category-paneliste.component';
import { CategoryListPanelisteComponent } from './category/category-list-paneliste/category-list-paneliste.component';
import { AnnouncePanelisteComponent } from './Announce/announce-paneliste/announce-paneliste.component';
import { OwnerFeedbackListComponent } from './feedback/owner-feedback-list/owner-feedback-list/owner-feedback-list.component';

import { ChatComponent } from './chat/chat.component';
import { HomeAnnonceurComponent } from './home/home-annonceur/home-annonceur.component';
import { annonceurGuard } from './auth/annonceur.guard';
import { panelisteGuard } from './auth/paneliste.guard';
import { HomePanelisteComponent } from './home/home-paneliste/home-paneliste/home-paneliste.component';
import { ProfileComponent } from './profile/profile-component/profile/profile.component';
import { ReclamationListComponent } from './reclamation/reclamation-list/reclamation-list.component';
import { MyRewardsComponent } from './rewards/my-rewards/my-rewards/my-rewards.component';
import { NotificationPageComponent } from './notifications/notification-page/notification-page.component';
import { CalendarComponent } from './calendar/calendar.component';
export const routes: Routes = [
{
    path: 'paneliste/home',
    canActivate: [panelisteGuard],
    loadComponent: () =>
      import('./home/home-paneliste/home-paneliste/home-paneliste.component')
        .then(m => m.HomePanelisteComponent)
  },
    {
    path: '',
    loadComponent: () =>
      import('./home/home/home.component').then(m => m.HomeComponent),
    pathMatch: 'full',
  },
 { path: '', component: HomeComponent , pathMatch: 'full'}, 
  /* { path: '',
    loadComponent: () =>
      import('./home/home/home.component').then(m => m.HomeComponent),
  },*/
  { path: 'home', redirectTo: '' },
    //{path:'aa' ,component:SignUpComponent},
   //{path:'SignIn' ,component:SignInComponent},
 { path: 'annonceur/home', component: HomeAnnonceurComponent, canActivate: [annonceurGuard] },
  { path: 'addAnnounce',    component: AddAnnounceComponent,    canActivate: [annonceurGuard] },
//{ path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: SignInComponent },
  { path: 'sign-in', redirectTo: 'login' },  // Redirection pour compatibilité
  { path: 'register', component: SignUpComponent },
  { path: 'verify', component: VerifyEmailComponent },     // /verify?code=XXXXXX
  { path: 'forgot', component: ForgotPasswordComponent },
  { path: 'reset', component: ResetPasswordComponent },    // /reset?code=XXXXXX
 // { path: '**', redirectTo: 'login' },


  { path: 'chat', component: ChatComponent },


   //Announcement
   {path:'announces' , component:AnouncesComponent, canActivate: [annonceurGuard]},
   {path:'addAnnounce' , component:AddAnnounceComponent, canActivate: [annonceurGuard]},
   {path:'uploadAnnouncePhoto/:id' , component:UploadFileComponent},
   {path:'announcements', component:ListAnnounceComponent},
   { path: 'announcement/edit/:id', component: EditAnnounceComponent },
   { path: 'announcement/update/:id', component: UpdateAnnounceComponent },
{ path: 'announcement/details/:id', component: DetailsAnnounceComponent },
  { path: 'announcementPaneliste/details/:id', component: AnnouncePanelisteComponent },

   // Campagne
   {path:'addCampagne' , component:CampagneAddComponent},
   {path:'CampagneList' , component:CampagneListComponent},

   //category
   
   //{path:'CategoryList' , component:CategoryListComponent},
   { path: 'categories/:id', component: CategoryDetailsComponent },
{ path: 'CategoryList', loadComponent: () => import('./category/category-list/category-list.component').then(m => m.CategoryListComponent) },
      { path: 'categoriesPaneliste/:id', component: CategoryPanelisteComponent },
         { path: 'categoriesPaneliste', component: CategoryListPanelisteComponent },

   //question
   {path:'addQuestion' , component:QuestionAddComponent},
   {path:'QuestionList' , component:QuestionListComponent},
    //quiz
   //{path:'addQuiz' , component:QuizAddComponent},
//{path:'QuizList' , component:QuizListComponent},
   { path: 'add', component: QuizAddComponent },
  {path:"quiz", component:QuizListComponent},
 { path: 'edit/:id', component: QuizAddComponent },
 { path: 'quiz/edit/:id', loadComponent: () => import('./quiz/edit-quiz/edit-quiz.component').then(m => m.EditQuizComponent) },
 { path: 'quiz/details/:id',component: QuizDetailsComponent },
 { path: 'take-quiz/:announceId/:quizId', loadComponent: () => import('./participation/take-quiz/take-quiz.component').then(m => m.TakeQuizComponent) },

    //reclamation
   //{path:'addReclamation' , component:ReclamationAddComponent},

   //{ path: 'editReclamation/:id', component: ReclamationAddComponent },
    {
    path: 'reclamations',
    loadComponent: () =>
      import('./reclamation/reclamation-list/reclamation-list.component')
        .then(c => c.ReclamationListComponent)
  },
 {
    path: 'reclamations/new',
    loadComponent: () =>
      import('./reclamation/reclamation-add/reclamation-add.component')
        .then(c => c.ReclamationAddComponent)
  },

  // Édition d’une réclamation (même composant, mode édition via l’id)
  {
    path: 'reclamations/:id/edit',
    loadComponent: () =>
      import('./reclamation/reclamation-add/reclamation-add.component')
        .then(c => c.ReclamationAddComponent)
  },
    //recompenses
   {path:'addRecompense' , component:RecompensesAddComponent},
   {path:'recompenses' , component:RecompensesListComponent, canActivate: [annonceurGuard]},
   {path:'RecompensesList' , component:RecompensesListComponent},
   {path: 'recompenses/edit/:id',  loadComponent: () => import('./recompenses/edit-recompenses/edit-recompenses.component').then(m => m.EditRecompensesComponent)},
      
     {
    path: 'announcement/:id/results',
    loadComponent: () =>
      import('./participation/announcement-results/announcement-results.component')
        .then(m => m.AnnouncementResultsComponent)
  },
  {
  path: 'announcements/:id/feedbacks',
  loadComponent: () =>
    import('./feedback/owner-feedback-list/owner-feedback-list/owner-feedback-list.component')
      .then(m => m.OwnerFeedbackListComponent),
  },

  {
    path: 'notifications',
    loadComponent: () =>
      import('./notifications/notification-page/notification-page.component')
        .then(m => m.NotificationPageComponent)
  },
{ path: 'me/rewards', component: MyRewardsComponent },

   {
  path: 'owner/rewards',
  title: 'Mes récompenses',
  canActivate: [annonceurGuard],
  loadComponent: () =>
    import('./rewards/owner-rewards-Component/owner-rewards/owner-rewards.component')
      .then(m => m.OwnerRewardsComponent)
},
{
  path: 'owner/announces/:id/rewards',
  title: 'Récompenses de l’annonce',
  canActivate: [annonceurGuard],
  loadComponent: () =>
    import('./rewards/owner-rewards-Component/owner-rewards/owner-rewards.component')
      .then(m => m.OwnerRewardsComponent)
},


 { path: 'calendar', component: CalendarComponent },
{ path: 'profile', component: ProfileComponent },
   //{ path: 'announcementss/:id/feedbacks', component: FeedbackListComponent },
     { path: 'take-quiz/:announceId/:quizId',loadComponent: () =>  import('./participation/take-quiz/take-quiz.component').then(m => m.TakeQuizComponent),},
    {path: 'announcements/:id/feedbacks/new',loadComponent: () =>import('./feedback/feedback-form/feedback-form.component') .then(m => m.FeedbackFormComponent),},
   // {path: 'announcement/:id/feedbacks', loadComponent: () => import('./feedback/feedback-list/feedback-list.component').then(m => m.FeedbackListComponent)}
];
