import { NgClass, CommonModule } from '@angular/common';
import { HeaderComponent } from "../header/header.component";
import { Component, OnInit, inject } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { forkJoin } from 'rxjs';
import { SummaryService } from '../summary/summary.service';
import { LoadingValueComponent } from '../loading-value/loading-value.component';
import { SummaryPeriod } from '../summary/summary-period';
import { RouterLink } from '@angular/router';
import { HideValueComponent } from "../hide-value/hide-value.component";
import { ToggleVisibilityService } from '../hide-value/toggle-visibility.service';
import { UtilsService } from '../utils/utils.service';
import { DateService } from '../utils/date.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [LoadingValueComponent, NgClass, HeaderComponent, CommonModule, RouterLink, HideValueComponent],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  svgContent: SafeHtml = "";
  svgContent2: SafeHtml = "";

  hiddenValue = '*****';
  hidden = false;
  opacity: number = 1;

  noTransactions = false;

  // Pie chart
  savingsAverage: string = '';
  savingsPercentage = 0;
  savingsPercentageFormated = '';
  savingsTrendPercentage = '';

  // Net worth
  netWorth: string = '';
  totalAssets: string = '';
  totalLiabilities: string = '';

  // Line graph
  lineChartHeights: number[] = [];
  mainPath: string = '';
  zeroPath: string = '';
  netWorthTrendChange: string = '';
  netWorthTrendPercentage = '';

  // Savings Trend graph
  balanceLastMonth = '';
  balanceLastMonthPercentage = '';

  // Bar graphs
  pastIncomeBarHeights = [{ height: '', value: '' }];
  pastExpensesBarHeights = [{ height: '', value: '' }];
  futureIncomeBarHeights = [{ height: '', value: '' }];
  futureExpensesBarHeights = [{ height: '', value: '' }];
  currentIncomeFormated = '';
  currentExpenseFormated = '';
  lastMonthIncomeValue = '';
  lastMonthIncomePercentage = '';
  nextMonthIncomeValue = '';
  nextMonthIncomePercentage = '';
  lastMonthExpenseValue = '';
  lastMonthExpensePercentage = '';
  nextMonthExpenseValue = '';
  nextMonthExpensePercentage = '';

  values: number[] = [];
  values2 = [30, 45, 80, 60, 20, 90, 40, -70, 50, 85, 40, 60];

  graphWidth = 500;
  graphHeight = 300;
  padding = { top: 25, right: 25, bottom: 25, left: 25 };
  chartWidth = this.graphWidth - this.padding.left - this.padding.right;
  chartHeight = this.graphHeight - this.padding.top - this.padding.bottom;

  // Mock transaction data
  recentTransactions = [
    { date: '2024-07-15', category: 'WIP', description: 'Em Construção', amount: "-85.50" },
    { date: '2024-07-14', category: 'WIP', description: 'Em Construção', amount: "5000.00" },
    { date: '2024-07-12', category: 'WIP', description: 'Em Construção', amount: "-1500.00" },
    { date: '2024-07-10', category: 'WIP', description: 'Em Construção', amount: "-60.00" },
    { date: '2024-07-08', category: 'WIP', description: 'Em Construção', amount: "-120.00" },
  ];

  currentDate = new Date();
  currentYear = this.currentDate.getFullYear();
  currentMonth = this.currentDate.getMonth();

  toggleService = inject(ToggleVisibilityService);
  reportsService = inject(SummaryService);
  utilsService = inject(UtilsService);
  dateService = inject(DateService);
  sanitizer = inject(DomSanitizer);
  
  ngOnInit(): void {
    this.reportsService.getAccountSummary().subscribe((accountSummary) => {
      this.netWorth = this.utilsService.formatCurrency(accountSummary.totalBalance);
      this.totalAssets = this.utilsService.formatCurrency(accountSummary.totalIncome);
      this.totalLiabilities = this.utilsService.formatCurrency(accountSummary.totalExpense);
      if (accountSummary.totalIncome == 0 && accountSummary.totalExpense == 0)
        this.noTransactions = true;
    });

    let startDate = new Date(this.currentDate.getFullYear(), 0, 1);
    let dateFrom = this.dateService.removeMonths(startDate, 12);
    let dateTo = this.dateService.removeMonths(startDate, 1);
    const requests = [];
    for (let i = 1; i <= 5; i++) {
      requests.push(this.reportsService.getSummaryByDate(dateFrom, dateTo));
      dateFrom = this.dateService.removeMonths(dateFrom, 12);
      dateTo = this.dateService.removeMonths(dateTo, 12);
    }

    let balanceYearsList: number[] = [];
    let yearsWithTransactions = 0;
    let totalBalanceFiveYears = 0;
    let totalIncomeFiveYears = 0;

    // Send all http requests and wait
    forkJoin(requests).subscribe((results: SummaryPeriod[]) => {
      results.forEach((summaryPreviousYears) => {
        balanceYearsList.push(summaryPreviousYears.totalBalancePeriod);
        // Savings:
        if (summaryPreviousYears.totalIncomePeriod != 0 && summaryPreviousYears.totalExpensePeriod != 0) {
          totalBalanceFiveYears += summaryPreviousYears.totalBalancePeriod;
          totalIncomeFiveYears += summaryPreviousYears.totalIncomePeriod;
          yearsWithTransactions++;
        }
      })
      if (yearsWithTransactions > 0) {
        let totalSavingsAverage = totalBalanceFiveYears / yearsWithTransactions
        this.savingsAverage = this.utilsService.formatCurrency(totalSavingsAverage);
        this.savingsPercentage = this.utilsService.getPercentage(totalBalanceFiveYears, totalIncomeFiveYears);
        this.savingsPercentageFormated = this.utilsService.formatPercentage(this.savingsPercentage);
        let savingsBeforeLastYear = totalBalanceFiveYears - balanceYearsList[0];
        this.savingsTrendPercentage = this.utilsService.percentageChangeFormated(totalSavingsAverage, savingsBeforeLastYear);
      } else {
        this.savingsAverage = this.utilsService.formatCurrency(0);
        this.savingsPercentage = 0;
        this.savingsPercentageFormated = this.utilsService.formatPercentage(0);
      }

      this.getLineChartHeights(balanceYearsList);
      this.zeroPath = 'M 25 0 L 75 0.001 L 125 0 L 175 0 L 225 0 L 275 0 L 325 0 L 375 0 L 425 0 L 475 0 L 525 0 L 575 0';
      this.mainPath = `M 25 ${this.lineChartHeights[4]} L 75 ${this.lineChartHeights[4]} L 125 ${this.lineChartHeights[4]}` +
        ` L 175 ${this.lineChartHeights[3]} L 225 ${this.lineChartHeights[3]} L 275 ${this.lineChartHeights[2]}` +
        ` L 325 ${this.lineChartHeights[2]} L 375 ${this.lineChartHeights[1]} L 425 ${this.lineChartHeights[1]}` +
        ` L 475 ${this.lineChartHeights[1]} L 525 ${this.lineChartHeights[0]} L 575 ${this.lineChartHeights[0]}`;
      this.netWorthTrendChange = this.utilsService.formatCurrency(balanceYearsList[0]);
      this.netWorthTrendPercentage = this.utilsService.percentageChangeFormated(balanceYearsList[0], balanceYearsList[1]);
    });

    // 12 Months before:
    let last12Months = this.dateService.removeMonths(this.currentDate, 12);
    let futureDate = this.dateService.addMonths(this.currentDate, 6);
    let incomeListPast: number[] = [];
    let expenseListPast: number[] = [];
    let incomeListFuture: number[] = [];
    let expenseListFuture: number[] = [];

    this.reportsService.getSummaryByDate(last12Months, futureDate).subscribe((summaryRecentMonths) => {
      for (let i = 0; i < summaryRecentMonths.summaryList.length; i++) {
        let monthSummary = summaryRecentMonths.summaryList[i];
        if (i < 12) {
          let totalIncome = monthSummary.summary.totalIncome;
          let totalExpense = monthSummary.summary.totalExpense;
          let totalBalance = monthSummary.summary.totalBalance;
          incomeListPast.push(totalIncome);
          expenseListPast.push(totalExpense);
          if (totalIncome > 0 && totalExpense == 0) {
            this.values.push(100);
            continue;
          }
          if (totalExpense > 0 && totalIncome == 0) {
            this.values.push(-100);
            continue;
          }
          if (totalExpense == 0 && totalIncome == 0) {
            this.values.push(0);
            continue;
          }
          let balance = Math.round(this.utilsService.getPercentage(totalBalance, totalIncome));
          this.values.push(balance);
        } else {
          incomeListFuture.push(monthSummary.summary.totalIncome);
          expenseListFuture.push(monthSummary.summary.totalExpense);
        }
      }

      this.updateGraph();

      let currentIncome = incomeListFuture[0];
      let currentExpense = expenseListFuture[0];

      this.currentIncomeFormated = this.utilsService.formatCurrency(currentIncome);
      this.currentExpenseFormated = this.utilsService.formatCurrency(currentExpense);

      // only last 6 months
      this.pastIncomeBarHeights = this.getIncomeExpenseBarHeights(incomeListPast.slice(6, 12));
      this.pastExpensesBarHeights = this.getIncomeExpenseBarHeights(expenseListPast.slice(6, 12));

      this.lastMonthIncomeValue = this.utilsService.formatCurrency(incomeListPast[11]);
      this.lastMonthIncomePercentage = this.utilsService.percentageChangeFormated(currentIncome, incomeListPast[11]);


      this.lastMonthExpenseValue = this.utilsService.formatCurrency(expenseListPast[11]);
      this.lastMonthExpensePercentage = this.utilsService.percentageChangeFormated(currentExpense, expenseListPast[11]);

      let balanceLastMonth = incomeListPast[11] - expenseListPast[11];
      this.balanceLastMonth = this.utilsService.formatCurrency(balanceLastMonth);
      this.balanceLastMonthPercentage = this.utilsService.percentageChangeFormated(balanceLastMonth, incomeListPast[10] - expenseListPast[10]);

      this.nextMonthIncomeValue = this.utilsService.formatCurrency(incomeListFuture[1]);
      this.nextMonthIncomePercentage = this.utilsService.percentageChangeFormated(incomeListFuture[1], currentIncome);
      this.nextMonthExpenseValue = this.utilsService.formatCurrency(expenseListFuture[1]);
      this.nextMonthExpensePercentage = this.utilsService.percentageChangeFormated(expenseListFuture[1], currentExpense);

      incomeListFuture.shift();
      expenseListFuture.shift();
      this.futureIncomeBarHeights = this.getIncomeExpenseBarHeights(incomeListFuture);
      this.futureExpensesBarHeights = this.getIncomeExpenseBarHeights(expenseListFuture);
    });
  }

  // Get the biggest value and consider it 100%, and then calculate the percentage for each month
  private getLineChartHeights(list: any): void {
    let maxBalance = Math.max(...list);
    if (maxBalance <= 0) {
      maxBalance = Math.min(...list);
      list.map((balance: number) => {
        let percentage = this.utilsService.getPercentageChange(balance, maxBalance);
        this.lineChartHeights.push(percentage + 100);
      });
    } else {
      list.map((balance: number) => {
        // Calculate the percentage height based on the maximum balances
        let percentage = this.utilsService.getPercentageChange(balance, maxBalance) * -1;
        this.lineChartHeights.push(percentage - 100);
      });
    }
  }

  // Get the biggest value and consider it 100%, and then calculate the percentage for each month
  private getIncomeExpenseBarHeights(list: any): any {
    let valueBarHeights: any = [];
    let maxValue = Math.max(...list);
    if (maxValue == 0) list = [-100, -100, -100, -100, -100, -100]; // To not show bars when empty
    list.map((value: number) => {
      let balanceCurrency = this.utilsService.formatCurrency(value);
      // Calculate the percentage height based on the maximum balances
      let percentage = (100 + this.utilsService.getPercentageChange(value, maxValue));
      valueBarHeights.push({ height: percentage, value: balanceCurrency });
    });
    return valueBarHeights;
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

    const points = values.map((val, i) => ({
      x: this.padding.left + (i * (this.chartWidth / 11)),
      y: this.padding.top + this.chartHeight / 2 - (val / 100 * this.chartHeight / 2)
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
      svg += `<circle cx="${point.x}" cy="${point.y}" r="4" class="data-point"></circle>
              <text x="${point.x}" y="${this.graphHeight - this.padding.bottom + 15}" class="label" text-anchor="middle">
                ${this.dateService.getRelativeMonthName(this.currentMonth + i - 1)}
              </text>
              <text x="${point.x}" y="${point.y - 10}" class="label" font-weight="bold" text-anchor="middle">
                ${values[i]}
              </text>`;
    });
    return this.sanitizer.bypassSecurityTrustHtml(svg);
  }

  private updateGraph(): void {
    this.svgContent = this.drawGraph(this.values);
    this.svgContent2 = this.drawGraph(this.values2);
  }

  toggleValues() {
    this.hidden = this.toggleService.isHidden;
    this.fadeIn();
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

  getChangeColorGood(change: string): string {
    return this.utilsService.getChangeColorGood(change);
  }

  getChangeColorBad(change: string): string {
    return this.utilsService.getChangeColorBad(change);
  }

  getChangeColorPie(change: number): string {
    if (change < 0) return 'negative';
    if (change > 0) return 'positive';
    return 'neutral';
  }

  getRelativeMonthName(n: number) : string {
    return this.dateService.getRelativeMonthName(n);
  }  
}
