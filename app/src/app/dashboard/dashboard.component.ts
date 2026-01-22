import { CurrencyPipe, NgClass, NgFor } from '@angular/common';
import { HeaderComponent } from "../header/header.component";
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { firstValueFrom } from 'rxjs';
import { ReportsService } from '../reports/reports.service';
import { SummaryByDate } from '../reports/summary-by-date';
import { SummaryLastYear } from '../reports/summary-last-year';
import { SummaryBasic } from './summary-basic';
import { dateTimestampProvider } from 'rxjs/internal/scheduler/dateTimestampProvider';

interface Transaction {
  date: string;
  category: string;
  description: string;
  amount: string;
}
@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CurrencyPipe, NgClass, NgFor, HeaderComponent, CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  svgContent: SafeHtml = "";
  svgContent2: SafeHtml = "";

  // TODO mudar para componente
  hiddenValue = '*****';
  hidden = false;
  opacity: number = 1;

  constructor(private sanitizer: DomSanitizer) {
  }
  netWorth: string = '';
  totalAssets: string = '';
  totalLiabilities: string = '';

  savingsAverage: string = '0';
  savingsPercentage = 0;
  savingsPercentageFormated = '';
  savingsTrendPercentage = '';

  lineChartHeights: number[] = [];
  dPath: string = '';

  pastIncomeBarHeights = [{ height: '', value: '' }];
  pastExpensesBarHeights = [{ height: '', value: '' }];
  futureIncomeBarHeights = [{ height: '', value: '' }];
  futureExpensesBarHeights = [{ height: '', value: '' }];

  lastMonthIncomeValue = '';
  lastMonthIncomePercentage = '';
  nextMonthIncomeValue = '';
  nextMonthIncomePercentage = '';
  lastMonthExpenseValue = '';
  lastMonthExpensePercentage = '';
  nextMonthExpenseValue = '';
  nextMonthExpensePercentage = '';



  incomeExpenseChange: string = '2500';
  incomeExpensePercentage: string = '15';

  netWorthTrendChange: string = '';
  netWorthTrendPercentage = '';

  reportsService = inject(ReportsService);

  hide() {
    this.fadeIn();
    this.hidden = !this.hidden;
  }

  fadeIn(): void {
    this.opacity = 0;
    const step = () => {
      this.opacity += 0.015;
      if (this.opacity < 1) {
        requestAnimationFrame(step);
      }
    };

    requestAnimationFrame(step);
  }
  // Mock transaction data
  recentTransactions: Transaction[] = [
    { date: '2024-07-15', category: 'Groceries', description: 'Supermarket purchase', amount: "-85.50" },
    { date: '2024-07-14', category: 'Salary', description: 'Monthly paycheck', amount: "5000.00" },
    { date: '2024-07-12', category: 'Rent', description: 'Apartment rent', amount: "-1500.00" },
    { date: '2024-07-10', category: 'Dining', description: 'Restaurant dinner', amount: "-60.00" },
    { date: '2024-07-08', category: 'Utilities', description: 'Electricity bill', amount: "-120.00" },
  ];

  getAmountClass(amountString: string): string {
    let amount = Number.parseFloat(amountString);
    if (amount > 0) {
      return 'green';
    }
    if (amount < 0) {
      return 'red';
    }
    return '';
  }

  formatAmount(amount: number): string {
    const formatted = amount.toFixed(2);
    if (amount > 0) {
      return `+${formatted}`;
    }
    return formatted;
  }

  months = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  values = [50, 65, 60, 100, 0, 40, 78, 55, 62, 78, 65, 48];
  values2 = [30, 45, 80, 60, 20, 90, 40, 70, 50, 85, 40, 60];

  graphWidth = 500;
  graphHeight = 300;
  padding = { top: 25, right: 25, bottom: 25, left: 25 };
  chartWidth = this.graphWidth - this.padding.left - this.padding.right;
  chartHeight = this.graphHeight - this.padding.top - this.padding.bottom;

  private async getSummaryByMonth(date: Date): Promise<SummaryBasic> {
    const summary = await firstValueFrom(this.reportsService.getSummaryByMonth(date));
    return summary;
  }

  private async getSummaryByDate(dateFrom: Date, dateTo: Date): Promise<SummaryBasic> {
    const summary = await firstValueFrom(this.reportsService.getSummaryByDate(dateFrom, dateTo));
    return summary;
  }

  private async getAccountSummary(): Promise<SummaryBasic> {
    const summary = await firstValueFrom(this.reportsService.getAccountSummary());
    return summary;
  }

  async ngOnInit(): Promise<void> {
    this.updateGraph();
    let currentDate = new Date();
    let dateFrom = new Date(currentDate.getFullYear() - 1, 0, 1);
    let dateTo = new Date(currentDate.getFullYear() - 1, 11, 1);

    let balanceList: number[] = [];
    let savingsPerYear: number[] = [];
    let yearsWithTransactions = 0;
    let totalBalanceFiveYears = 0;
    let totalIncomeFiveYears = 0;

    for (let i = 1; i <= 5; i++) {
      const byDateSummary = await this.getSummaryByDate(dateFrom, dateTo);
      balanceList.push(byDateSummary.balance);
      dateFrom.setFullYear(dateFrom.getFullYear() - 1);
      dateTo.setFullYear(dateTo.getFullYear() - 1);

      // Savings:
      if (byDateSummary.totalIncome == 0 && byDateSummary.totalExpense == 0) continue;
      savingsPerYear.push(byDateSummary.balance / byDateSummary.totalIncome);
      totalBalanceFiveYears += byDateSummary.balance;
      totalIncomeFiveYears += byDateSummary.totalIncome;
      yearsWithTransactions++;
    }

    if (yearsWithTransactions > 0) {
      this.savingsAverage = this.formatCurrency(totalBalanceFiveYears / yearsWithTransactions);
      this.savingsPercentage = totalBalanceFiveYears * 100 / totalIncomeFiveYears;
      this.savingsPercentageFormated = this.formatPercentage(this.savingsPercentage);
      let beforeLastYear = totalBalanceFiveYears - balanceList[0];
      this.savingsTrendPercentage = this.formatPercentage(this.getPercentageChange(totalBalanceFiveYears, beforeLastYear));
    } else {
      this.savingsAverage = this.formatCurrency(0);
      this.savingsPercentage = 100;
      this.savingsPercentageFormated = this.formatPercentage(0);
    }

    // 6 Months:
    let pastDate = new Date();
    pastDate.setMonth(pastDate.getMonth() - 6);
    let incomeListPast = [];
    let expenseListPast = [];
    for (let i = 0; i < 6; i++) {
      let monthSummary = await this.getSummaryByMonth(pastDate);
      pastDate.setMonth(pastDate.getMonth() + 1);
      incomeListPast.push(monthSummary.totalIncome);
      expenseListPast.push(monthSummary.totalExpense);
    }
    let futureDate = new Date();
    let incomeListFuture = [];
    let expenseListFuture = [];
    for (let i = 0; i < 6; i++) {
      let monthSummary = await this.getSummaryByMonth(futureDate);
      futureDate.setMonth(futureDate.getMonth() + 1);
      incomeListFuture.push(monthSummary.totalIncome);
      expenseListFuture.push(monthSummary.totalExpense);
    }
    this.pastIncomeBarHeights = this.getIncomeExpenseBarHeights(incomeListPast);
    this.pastExpensesBarHeights = this.getIncomeExpenseBarHeights(expenseListPast);

    this.lastMonthIncomeValue = this.formatCurrency(incomeListPast[5]);
    this.lastMonthIncomePercentage = this.formatPercentage(this.getPercentageChange(incomeListFuture[0], incomeListPast[5]));
    debugger;
    this.lastMonthExpenseValue = this.formatCurrency(expenseListPast[5]);
    this.lastMonthExpensePercentage = this.formatPercentage(this.getPercentageChange(expenseListFuture[0], expenseListPast[5]));

    this.futureIncomeBarHeights = this.getIncomeExpenseBarHeights(incomeListFuture);
    this.futureExpensesBarHeights = this.getIncomeExpenseBarHeights(expenseListFuture);

    this.nextMonthIncomeValue = this.formatCurrency(incomeListFuture[1]);
    this.nextMonthIncomePercentage = this.formatPercentage(this.getPercentageChange(incomeListFuture[1], incomeListFuture[0]));
    this.nextMonthExpenseValue = this.formatCurrency(expenseListFuture[1]);
    this.nextMonthExpensePercentage = this.formatPercentage(this.getPercentageChange(expenseListFuture[1], expenseListFuture[0]));

    const accountSummary = await this.getAccountSummary();
    this.netWorth = this.formatCurrency(accountSummary.balance);
    this.totalAssets = this.formatCurrency(accountSummary.totalIncome);
    this.totalLiabilities = this.formatCurrency(accountSummary.totalExpense);

    this.getLineChartHeights(balanceList);
    this.dPath = 'M 25 ' + this.lineChartHeights[4] + ' L 75 ' + this.lineChartHeights[3] + ' L 125 ' + this.lineChartHeights[2] + ' L 175 ' + this.lineChartHeights[1] + ' L 225 ' + this.lineChartHeights[0];
    this.netWorthTrendChange = this.formatCurrency(balanceList[0] - balanceList[1]);
    this.netWorthTrendPercentage = this.formatPercentage(this.getPercentageChange(balanceList[0], balanceList[1]));
  }

  // Get the biggest value and consider it 100%, and then calculate the percentage for each month
  private getLineChartHeights(list: any): void {
    let maxBalance = Math.max(...list);
    list.map((balance: number) => {
      // Calculate the percentage height based on the maximum balances
      let percentage = this.getPercentageChange(balance, maxBalance) * -1;
      this.lineChartHeights.push(percentage);
    });
  }

  // Get the biggest value and consider it 100%, and then calculate the percentage for each month
  private getIncomeExpenseBarHeights(list: any): any {
    let valueBarHeights: any = [];
    let maxValue = Math.max(...list);
    list.map((value: number) => {
      let balanceCurrency = this.formatCurrency(value);
      // Calculate the percentage height based on the maximum balances
      let percentage = (100 + this.getPercentageChange(value, maxValue));
      valueBarHeights.push({ height: percentage, value: balanceCurrency });
    });
    return valueBarHeights;
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  // TODO refactor to utility service
  getPercentageChange(num1: number, num2: number): number {
    let difference = num1 - num2;
    return difference * 100 / num2;
  }

  formatPercentage(diffPercent: number): string {
    if (!isFinite(diffPercent)) return '0%';
    // 2 decimal places for percentage
    diffPercent = Math.round(diffPercent * 100) / 100;
    return diffPercent > 0 ? `+${diffPercent}%` : `${diffPercent}%`;
  }

  drawGraph(values: number[]): SafeHtml {
    let svg = '';

    svg += `<defs>
              <filter id="f1" y="0">
                <feOffset in="SourceGraphic" dx="0" dy="15" />
                <feGaussianBlur stdDeviation="10" />
                <feBlend in="SourceGraphic" in2="blurOut" />
              </filter>
            </defs>`

    // Calculate positions
    const points = values.map((val, i) => ({
      x: this.padding.left + (i * (this.chartWidth / 11)),
      y: this.padding.top + this.chartHeight - (val / 100 * this.chartHeight)
    }));

    // Create smooth curve path
    let pathD = `M ${points[0].x},${points[0].y}`;
    for (let i = 0; i < points.length - 1; i++) {
      const current = points[i];
      const next = points[i + 1];
      const controlX = current.x + (next.x - current.x) / 2;
      pathD += ` C ${controlX},${current.y} ${controlX},${next.y} ${next.x},${next.y}`;
    }

    // Draw line
    svg += `<path d="${pathD}" class="data-line" filter="url(#f1)"></path>`;

    // Draw points and labels
    points.forEach((point, i) => {
      svg += `<circle cx="${point.x}" cy="${point.y}" r="4" class="data-point"></circle>`;
      svg += `<text x="${point.x}" y="${this.graphHeight - this.padding.bottom + 15}" class="label" text-anchor="middle">${this.months[i]}</text>`;
      svg += `<text x="${point.x}" y="${point.y - 10}" class="label" font-weight="bold" text-anchor="middle">${values[i]}</text>`;
    });
    return this.sanitizer.bypassSecurityTrustHtml(svg);
  }

  updateGraph(): void {
    this.svgContent = this.drawGraph(this.values);
    this.svgContent2 = this.drawGraph(this.values2);
  }
}
