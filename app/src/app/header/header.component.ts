
import { Component, OnInit, OnDestroy, inject } from '@angular/core';
import { Router, NavigationEnd, RouterLink } from '@angular/router';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { ProfileMenuComponent } from "./profile-menu/profile-menu.component";
import { AuthService } from '../auth/auth.service';

interface NavItem {
  label: string;
  link: string;
  active?: boolean;
  image: string;
}

interface HeaderConfig {
  routePattern: string;
  showAppNav: boolean;
  showLoginAction: boolean;
  showRegisterAction: boolean;
  showTestAction: boolean;
  showProfileActions: boolean;
  activeNavGroup: 'app' | null; // Indicates which nav list is potentially active
  activeNavLink?: string; // The specific link (from the active group) to mark as active
}

@Component({
  selector: 'app-header',
  imports: [RouterLink, ProfileMenuComponent],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit, OnDestroy {

  showAppNav: boolean = false;
  showLoginAction: boolean = false;
  showRegisterAction: boolean = false;
  showTestAction: boolean = false;
  showProfileActions: boolean = false;

  appNavItems: NavItem[] = [
    { label: 'Dashboard', link: '/dashboard', image: "M600-160v-280h280v280H600ZM440-520v-280h440v280H440ZM80-160v-280h440v280H80Zm0-360v-280h280v280H80Zm440-80h280v-120H520v120ZM160-240h280v-120H160v120Zm520 0h120v-120H680v120ZM160-600h120v-120H160v120Zm360 0Zm-80 240Zm240 0ZM280-600Z" },
    { label: 'Transação', link: '/add-transaction', image: "M200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v268q-19-9-39-15.5t-41-9.5v-243H200v560h242q3 22 9.5 42t15.5 38H200Zm0-120v40-560 243-3 280Zm80-40h163q3-21 9.5-41t14.5-39H280v80Zm0-160h244q32-30 71.5-50t84.5-27v-3H280v80Zm0-160h400v-80H280v80ZM720-40q-83 0-141.5-58.5T520-240q0-83 58.5-141.5T720-440q83 0 141.5 58.5T920-240q0 83-58.5 141.5T720-40Zm-20-80h40v-100h100v-40H740v-100h-40v100H600v40h100v100Z" },
    { label: 'Relatório', link: '/reports', image: "M280-280h80v-200h-80v200Zm320 0h80v-400h-80v400Zm-160 0h80v-120h-80v120Zm0-200h80v-80h-80v80ZM200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h560q33 0 56.5 23.5T840-760v560q0 33-23.5 56.5T760-120H200Zm0-80h560v-560H200v560Zm0-560v560-560Z" },
    { label: 'Pesquisar', link: '/search', image: "M400-320q100 0 170-70t70-170q0-100-70-170t-170-70q-100 0-170 70t-70 170q0 100 70 170t170 70Zm-40-120v-280h80v280h-80Zm-140 0v-200h80v200h-80Zm280 0v-160h80v160h-80ZM824-80 597-307q-41 32-91 49.5T400-240q-134 0-227-93T80-560q0-134 93-227t227-93q134 0 227 93t93 227q0 56-17.5 106T653-363l227 227-56 56Z" }
  ];

  // Define header states for different route patterns.
  readonly headerConfigs: HeaderConfig[] = [
    {
      routePattern: '/dashboard',
      showAppNav: true,
      showLoginAction: false,
      showRegisterAction: false,
      showProfileActions: true,
      activeNavGroup: 'app',
      activeNavLink: '/dashboard',
      showTestAction: false
    },
    {
      routePattern: '/add-transaction',
      showAppNav: true,
      showLoginAction: false,
      showRegisterAction: false,
      showProfileActions: true,
      activeNavGroup: 'app',
      activeNavLink: '/add-transaction',
      showTestAction: false
    },
    {
      routePattern: '/transactions/edit',
      showAppNav: true,
      showLoginAction: false,
      showRegisterAction: false,
      showProfileActions: true,
      activeNavGroup: 'app',
      activeNavLink: '/search',
      showTestAction: false
    },
    {
      routePattern: '/reports',
      showAppNav: true,
      showLoginAction: false,
      showRegisterAction: false,
      showProfileActions: true,
      activeNavGroup: 'app',
      activeNavLink: '/reports',
      showTestAction: false
    },
    {
      routePattern: '/search',
      showAppNav: true,
      showLoginAction: false,
      showRegisterAction: false,
      showProfileActions: true,
      activeNavGroup: 'app',
      activeNavLink: '/search',
      showTestAction: false
    },
    {
      routePattern: '/login',
      showAppNav: false,
      showLoginAction: false,
      showRegisterAction: true,
      showProfileActions: false,
      activeNavGroup: null,
      showTestAction: true
    },
    {
      routePattern: '/register',
      showAppNav: false,
      showLoginAction: true,
      showRegisterAction: false,
      showProfileActions: false,
      activeNavGroup: null,
      showTestAction: true
    },
    {
      routePattern: '/client/edit',
      showAppNav: true,
      showLoginAction: false,
      showRegisterAction: false,
      showProfileActions: true,
      activeNavGroup: null,
      showTestAction: false
    },
    {
      routePattern: '/',
      showAppNav: false,
      showLoginAction: false,
      showRegisterAction: false,
      showProfileActions: false,
      activeNavGroup: null,
      showTestAction: true
    },
    // Fallback configuration if no pattern matches (e.g., 404 page)
    // This config won't have a routePattern, or could have an empty string pattern.
    // We handle the fallback in the update method if find returns undefined.
  ];

  readonly destroy$ = new Subject<void>(); // Subject to signal unsubscription

  constructor(readonly router: Router) { }

  authService = inject(AuthService);

  ngOnInit(): void {
    // Listen for router events to determine the current page
    this.router.events.pipe(
      filter(event => event instanceof NavigationEnd), // Only care about navigation ending
      takeUntil(this.destroy$) // Automatically unsubscribe on component destroy
    ).subscribe((event: NavigationEnd) => {
      this.updateHeaderState(event.urlAfterRedirects); // Update state based on the final URL
    });

    this.updateHeaderState(this.router.url);
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private updateHeaderState(currentUrl: string): void {
    // Find the configuration that matches the current URL pattern
    // Using find returns the first match, so order in headerConfigs is important
    const matchedConfig = this.headerConfigs.find(config => currentUrl.startsWith(config.routePattern));

    const fallbackConfig: HeaderConfig = {
      routePattern: '',
      showAppNav: false,
      showLoginAction: false,
      showRegisterAction: false,
      showProfileActions: false,
      activeNavGroup: null,
      activeNavLink: undefined,
      showTestAction: false
    };

    const finalConfig = matchedConfig || fallbackConfig;

    this.showAppNav = finalConfig.showAppNav;
    this.showLoginAction = finalConfig.showLoginAction;
    this.showRegisterAction = finalConfig.showRegisterAction;
    this.showProfileActions = finalConfig.showProfileActions;
    this.showTestAction = finalConfig.showTestAction;

    this.updateNavActiveState(finalConfig.activeNavGroup, finalConfig.activeNavLink);
  }

  private updateNavActiveState(activeGroup: 'marketing' | 'app' | null, activeLink: string | undefined): void {
    this.appNavItems.forEach(item => item.active = false);

    if (activeGroup === 'app' && activeLink !== undefined) {
      const activeItem = this.appNavItems.find(item => item.link === activeLink);
      if (activeItem) {
        activeItem.active = true;
      }
    }
  }

  isLoading: boolean = false;

  demoAccount() {
    this.isLoading = true;
    let credentials = { email: 'test@email.com', password: 'teste', name: '', confirmPassword: '' };
    this.authService.login(credentials).subscribe({
      next: (response) => {
        console.log('Login successful', response);
        this.router.navigate(['/dashboard']);
        this.isLoading = false;
      },
      error: (error) => {
        console.log('Login failed', error);
        alert("Falha ao entrar, tente novamente mais tarde.");
        this.router.navigate(['/']);
        this.isLoading = false;
      }
    });
  }
}
