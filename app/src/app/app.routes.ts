import { Routes } from '@angular/router';
import { AddTransactionComponent } from './add-transaction/add-transaction.component';
import { DashboardComponent } from './dashboard/dashboard.component';
import { HomeComponent } from './home/home.component';
import { authGuard } from './auth/auth.guard';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ReportsComponent } from './reports/reports.component';

export const routes: Routes = [
    {path: "", component: HomeComponent},
    {path: "register", component: RegisterComponent},
    {path: "login", component: LoginComponent},
    {path: "add-transaction", component: AddTransactionComponent, canActivate: [authGuard]},
    {path: "dashboard", component: DashboardComponent, canActivate: [authGuard]},
    {path: "reports", component: ReportsComponent, canActivate: [authGuard]},
];
