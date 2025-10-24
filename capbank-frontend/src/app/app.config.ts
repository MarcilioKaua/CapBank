import { ApplicationConfig, provideBrowserGlobalErrorListeners, provideZoneChangeDetection, LOCALE_ID, DEFAULT_CURRENCY_CODE } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeuix/themes/aura';
import { routes } from './app.routes';
import { provideHttpClient } from '@angular/common/http';
import { registerLocaleData } from '@angular/common';              // ✅ importar
import localePt from '@angular/common/locales/pt';

registerLocaleData(localePt);  // registro simples


export const appConfig: ApplicationConfig = {
  providers: [
    provideHttpClient(),

    { provide: LOCALE_ID, useValue: 'pt-BR' },    // locale global
    { provide: DEFAULT_CURRENCY_CODE, useValue: 'BRL' }, // moeda padrão

    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    provideZoneChangeDetection({ eventCoalescing: true }), provideAnimationsAsync(),
        providePrimeNG({ 
            theme: {
                preset: Aura
            }
        }),

  ]
};
