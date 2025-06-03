import { Component, ElementRef, HostListener, inject } from '@angular/core';
import { AuthService } from '../../auth/auth.service';

@Component({
  selector: 'app-profile-menu',
  templateUrl: './profile-menu.component.html',
  styleUrls: ['./profile-menu.component.css']
})
export class ProfileMenuComponent {
  isOpen = false;

  authService = inject(AuthService);

  // ElementRef is needed to check if a click happened inside or outside the component
  constructor(private elementRef: ElementRef) {}

  toggleMenu() {
    this.isOpen = !this.isOpen;
  }

  // Listen for clicks anywhere on the document
  @HostListener('document:click', ['$event'])
  onClickOutside(event: Event) {
    // Check if the clicked element is *not* inside this component's host element
    if (!this.elementRef.nativeElement.contains(event.target as Node) && this.isOpen) {
      this.isOpen = false;
    }
  }

  selectOption(option: string) {
    console.log('Selected option:', option);
    // Handle the selected option
    switch (option) {
      case 'Logout':
        this.authService.logout();
    }

    this.isOpen = false;
  }
}