
import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, NavigationEnd, RouterLink } from '@angular/router';
import { Subject } from 'rxjs';
import { filter, takeUntil } from 'rxjs/operators';
import { ProfileMenuComponent } from "./profile-menu/profile-menu.component";

interface NavItem {
  label: string;
  link: string;
  active?: boolean;
}

interface HeaderConfig {
  routePattern: string;
  showAppNav: boolean;
  showLoginAction: boolean;
  showRegisterAction: boolean;
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

  // --- Component Properties (managed by updateHeaderState) ---
  showAppNav: boolean = false;
  showLoginAction: boolean = false;
  showRegisterAction: boolean = false;
  showProfileActions: boolean = false;

  appNavItems: NavItem[] = [
    { label: 'Dashboard', link: '/dashboard' },
    { label: 'Transação', link: '/add-transaction' },
    { label: 'Relatório', link: '/reports' },
  ];

  // Define header states for different route patterns.
  private headerConfigs: HeaderConfig[] = [
     {
        routePattern: '/dashboard',
        showAppNav: true,
        showLoginAction: false,
        showRegisterAction: false,
        showProfileActions: true,
        activeNavGroup: 'app',
        activeNavLink: '/dashboard',
      },
       {
        routePattern: '/add-transaction',
        showAppNav: true,
        showLoginAction: false,
        showRegisterAction: false,
        showProfileActions: true,
        activeNavGroup: 'app',
        activeNavLink: '/add-transaction',
      },
      {
        routePattern: '/reports',
        showAppNav: true,
        showLoginAction: false,
        showRegisterAction: false,
        showProfileActions: true,
        activeNavGroup: 'app',
        activeNavLink: '/reports',
      },
      {
        routePattern: '/login',
        showAppNav: false,
        showLoginAction: false,
        showRegisterAction: true,
        showProfileActions: false,
        activeNavGroup: null,
      },
      {
        routePattern: '/register',
        showAppNav: false,
        showLoginAction: true,
        showRegisterAction: false,
        showProfileActions: false,
        activeNavGroup: null
      },
      {
        routePattern: '/',
        showAppNav: false,
        showLoginAction: false,
        showRegisterAction: false,
        showProfileActions: false,
        activeNavGroup: null
      },
      // Fallback configuration if no pattern matches (e.g., 404 page)
      // This config won't have a routePattern, or could have an empty string pattern.
      // We handle the fallback in the update method if find returns undefined.
    ];

  private destroy$ = new Subject<void>(); // Subject to signal unsubscription

  constructor(private router: Router) { }

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
    // Clean up the subscription
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
    };

    const finalConfig = matchedConfig || fallbackConfig;

    this.showAppNav = finalConfig.showAppNav;
    this.showLoginAction = finalConfig.showLoginAction;
    this.showRegisterAction = finalConfig.showRegisterAction;
    this.showProfileActions = finalConfig.showProfileActions;

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
}