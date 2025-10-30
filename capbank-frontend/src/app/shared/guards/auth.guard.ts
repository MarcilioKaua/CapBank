import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = (route, state) => {
  const authService = inject(AuthService)
  const router = inject(Router)

  const token = authService.getToken();

  if(token){
    const payload = parseJwt(token)
    if(!payload?.exp){
      return true      
    }
  }
  router.navigate(['/login'])
  return false
};


function parseJwt(token:string):any{
  try{
    return JSON.parse(token)
  } catch(e){
    return null;
  }
}