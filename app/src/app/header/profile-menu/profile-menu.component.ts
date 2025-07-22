import { Component, ElementRef, HostListener, inject } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Client } from '../../client';
import { currentClient } from '../../auth/auth.guard';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile-menu',
  templateUrl: './profile-menu.component.html',
  styleUrls: ['./profile-menu.component.css']
})
export class ProfileMenuComponent {
  isOpen = false;
  client: Client | undefined;

  authService = inject(AuthService);

  // ElementRef is needed to check if a click happened inside or outside the component
  constructor(readonly elementRef: ElementRef, readonly router: Router) {}

  toggleMenu() {
    this.isOpen = !this.isOpen;
    this.client = currentClient;
  }

  // Listen for clicks anywhere on the document
  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    // Check if the clicked element is not inside this component's host element
    if (!this.elementRef.nativeElement.contains(event.target as Node) && this.isOpen) {
      this.isOpen = false;
    }
  }

  selectOption(option: string) {
    switch (option) {
      case 'Editar':
        this.router.navigateByUrl("client/edit");
        break;
      case 'Logout':
        this.authService.logout();
        break;
    }
    this.isOpen = false;
  }
}
