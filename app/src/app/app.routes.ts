import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard/dashboard.component';
import { HomeComponent } from './home/home.component';
import { authGuard } from './auth/auth.guard';
import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { ReportsComponent } from './reports/reports.component';
import { SearchTransactionsComponent } from './search-transactions/search-transactions.component';
import { EditProfileComponent } from './edit-profile/edit-profile.component';
import { AddTransactionComponent } from './add-transaction/add-transaction.component';
import { AddTransactionByAiComponent } from './add-transaction/add-transaction-by-ai/add-transaction-by-ai.component';
import { AddTransactionManuallyComponent } from './add-transaction/add-transaction-manually/add-transaction-manually.component';

export const routes: Routes = [
  { path: "", component: HomeComponent, title: "Finances APP" },
  { path: "register", component: RegisterComponent, title: "Finances APP" },
  { path: "client/edit", component: EditProfileComponent, title: "Editar Usuário" },
  { path: "login", component: LoginComponent, title: "Finances APP" },
  { path: "add-transaction", component: AddTransactionComponent, title: "Adicionar Transação", canActivate: [authGuard] },
  { path: "add-transaction-by-ai", component: AddTransactionByAiComponent, title: "Adicionar Transação", canActivate: [authGuard] },
  { path: "add-transaction-manually", component: AddTransactionManuallyComponent, title: "Adicionar Transação", canActivate: [authGuard] },
  { path: 'transactions/edit', component: AddTransactionManuallyComponent, title: "Editar Transação", canActivate: [authGuard] },
  { path: "dashboard", component: DashboardComponent, title: "Finances APP", canActivate: [authGuard] },
  { path: "reports", component: ReportsComponent, title: "Relatório", canActivate: [authGuard] },
  { path: "search", component: SearchTransactionsComponent, title: "Pesquisar", canActivate: [authGuard] }
];
