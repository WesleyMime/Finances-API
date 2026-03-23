import { Component, ElementRef, HostListener, inject } from '@angular/core';
import { AuthService } from '../../auth/auth.service';
import { Client } from '../../client';
import { Router } from '@angular/router';
import { ClientService } from '../../client.service';

@Component({
  selector: 'app-profile-menu',
  templateUrl: './profile-menu.component.html',
  styleUrls: ['./profile-menu.component.css']
})
export class ProfileMenuComponent {
  isOpen = false;
  client: Client | undefined;
  initial: string = '';

  clientService = inject(ClientService);
  authService = inject(AuthService);

  // ElementRef is needed to check if a click happened inside or outside the component
  constructor(readonly elementRef: ElementRef, readonly router: Router) {
    if (this.client) {
      this.initial = this.client.name.charAt(0);
      return;
    }

    this.clientService.getClient().subscribe({
      next: (result: Client) => {
        this.client = result;
        this.initial = result.name.charAt(0);
      },
      error: (err) => console.error('Failed to load client for profile menu', err)
    });
  }

  toggleMenu() {
    this.isOpen = !this.isOpen;
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
