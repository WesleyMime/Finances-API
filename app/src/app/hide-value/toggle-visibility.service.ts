import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ToggleVisibilityService {
  isHidden = false;

  toggle() {
    this.isHidden = !this.isHidden;
    return this.isHidden;
  }  
}
