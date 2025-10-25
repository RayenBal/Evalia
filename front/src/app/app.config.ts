/*import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import { provideClientHydration } from '@angular/platform-browser';
import { provideHttpClient ,withFetch} from '@angular/common/http';
export const appConfig: ApplicationConfig = {
providers: [provideRouter(routes), provideClientHydration(),provideHttpClient(withFetch())]
};*/




import { ChatComponent } from './chat/chat.component';

import { ApplicationConfig } from '@angular/core';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

import { provideClientHydration } from '@angular/platform-browser';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { authInterceptor } from './auth/auth-service/auth.interceptor'; 
import { provideAnimations } from '@angular/platform-browser/animations';

export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideClientHydration(),
    provideAnimations(),
    provideHttpClient(
      withFetch(),                         // (optionnel) utilise fetch
      withInterceptors([authInterceptor])  // <<< enregistrement de l’intercepteur
    ),
  ],
};

export default appConfig; // <<< export par défaut
