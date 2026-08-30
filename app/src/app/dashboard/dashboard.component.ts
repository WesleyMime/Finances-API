import { CurrencyPipe, DatePipe, NgClass } from '@angular/common';
import { HeaderComponent } from "../header/header.component";
import { Component, OnInit, inject } from '@angular/core';
import { forkJoin } from 'rxjs';
import { SummaryService } from '../summary/summary.service';
import { LoadingValueComponent } from '../loading-value/loading-value.component';
import { SummaryPeriod } from '../summary/summary-period';
import { RouterLink } from '@angular/router';
import { HideValueComponent } from "../hide-value/hide-value.component";
import { ToggleVisibilityService } from '../hide-value/toggle-visibility.service';
import { UtilsService } from '../utils/utils.service';
import { DateService } from '../utils/date.service';
import { Transaction } from '../add-transaction/transaction';
import { getCategoryByNameInEnglish } from '../category';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration } from 'chart.js';

const chartPalette = {
  positive: '#48bb78',
  negative: '#fc8181',
  neutral: '#2d3748',
  fill: '#48bb7865',
  muted_text_color: '#a0aec0'
};

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [LoadingValueComponent, NgClass, HeaderComponent, RouterLink, HideValueComponent, DatePipe, CurrencyPipe, BaseChartDirective],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {
  hiddenValue = '*****';
  hidden = false;
  opacity: number = 1;

  noTransactions = false;

  // Donut chart
  savingsAverage: string = '';
  savingsPercentage = 0;
  savingsPercentageFormated = '';
  savingsTrendPercentage = '';
  savingsDonutChartData: ChartConfiguration<'doughnut'>['data'] = {
    datasets: [{
      data: [0, 100],
      borderWidth: 1,
      backgroundColor: [chartPalette.positive, chartPalette.neutral]
    }]
  };
  donutChartOptions: ChartConfiguration<'doughnut'>['options'] = {
    animation: {
      duration: 5000
    },
    rotation: 0,
    cutout: '80%',
    plugins: {
      legend: { display: false }
    }
  };

  // Net worth
  netWorth: string = '';
  totalAssets: string = '';
  totalLiabilities: string = '';

  // Line graph
  netWorthTrendChange: string = '';
  netWorthTrendPercentage = '';
  financialEvolutionChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: ' Patrimônio',
      borderColor: chartPalette.positive,
      backgroundColor: chartPalette.fill,
      fill: true,
      tension: 0.35,
      pointRadius: 3,
      pointBackgroundColor: chartPalette.positive
    }]
  };
  trendChartOptions: ChartConfiguration<'line'>['options'] = {
    animation: {
      duration: 3600,
      easing: 'easeInOutQuad'
    },
    responsive: true,
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: {
      x: {
        display: true,
        grid: { display: false },
        ticks: { color: chartPalette.muted_text_color }
      },
      y: {
        display: false,
        beginAtZero: false,
        grid: { display: false },
        ticks: { color: chartPalette.muted_text_color }
      }
    }
  };

  // Savings Trend graph
  balanceLastMonth = '';
  balanceLastMonthPercentage = '';
  savingsTrendChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: ' Poupança',
      borderColor: chartPalette.positive,
      backgroundColor: chartPalette.fill,
      fill: true,
      tension: 0.35,
      pointRadius: 3,
      pointBackgroundColor: chartPalette.positive
    }]
  };

  // Investments Trend graph
  investmentsLastMonth = 'R$ 0';
  investmentsLastMonthPercentage = '0%';
  investmentsTrendChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: [{
      data: [],
      label: ' Investimentos',
      borderColor: chartPalette.positive,
      backgroundColor: chartPalette.fill,
      fill: true,
      tension: 0.35,
      pointRadius: 3,
      pointBackgroundColor: chartPalette.positive
    }]
  };

  // Bar graphs
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
  valuesTemp = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
  pastIncomeChartData: ChartConfiguration<'bar'>['data'] = {datasets: [] };
  pastExpensesChartData: ChartConfiguration<'bar'>['data'] = {datasets: [] };
  futureIncomeChartData: ChartConfiguration<'bar'>['data'] = {datasets: [] };
  futureExpensesChartData: ChartConfiguration<'bar'>['data'] = {datasets: [] };
  barChartOptions: ChartConfiguration<'bar'>['options'] = {
    responsive: true,
    animation: {
      duration: 3600,
      easing: 'easeInOutQuad'
    },
    maintainAspectRatio: false,
    plugins: { legend: { display: false } },
    scales: {
      x: {
        grid: { display: false } ,
        ticks: { color: chartPalette.muted_text_color }
      },
      y: {
        display: false
      }
    }
  };

  recentTransactions: Transaction[] = [];

  currentDate = new Date();
  currentYear = this.currentDate.getFullYear();
  currentMonth = this.currentDate.getMonth();

  summaryService = inject(SummaryService);
  toggleService = inject(ToggleVisibilityService);
  utilsService = inject(UtilsService);
  dateService = inject(DateService);

  ngOnInit(): void {
    this.hidden = this.toggleService.isHidden;
    this.updateTotalNetworth();

    let balanceLastFiveYearsList: number[] = [];
    let yearsWithTransactions = 0;
    let totalBalanceFiveYears = 0;
    let totalIncomeFiveYears = 0;

    // Send all http requests and wait
    const requests = this.requestsForLastFiveYears();
    forkJoin(requests).subscribe((results: SummaryPeriod[]) => {
      results.forEach((summary) => {
        balanceLastFiveYearsList.push(summary.totalBalancePeriod);
        if (summary.totalIncomePeriod != 0 && summary.totalExpensePeriod != 0) {
          totalBalanceFiveYears += summary.totalBalancePeriod;
          totalIncomeFiveYears += summary.totalIncomePeriod;
          yearsWithTransactions++;
        }
      })
      this.updateSavingsLastFiveYearsPieChart(yearsWithTransactions, totalBalanceFiveYears, totalIncomeFiveYears, balanceLastFiveYearsList);
      this.updateNetworthTrendChart(balanceLastFiveYearsList);
    });

    let incomeListPast: number[] = [];
    let expenseListPast: number[] = [];
    let incomeListFuture: number[] = [];
    let expenseListFuture: number[] = [];

    let lastTwelveMonths = this.dateService.removeMonths(this.currentDate, 12);
    let sixMonthsInTheFuture = this.dateService.addMonths(this.currentDate, 6);
    this.summaryService.getSummaryByDate(lastTwelveMonths, sixMonthsInTheFuture).subscribe((summaryRecentMonths) => {
      for (let i = 0; i < summaryRecentMonths.summaryList.length; i++) {
        let monthSummary = summaryRecentMonths.summaryList[i];
        if (i < 12) {
          let totalIncome = monthSummary.summary.totalIncome;
          let totalExpense = monthSummary.summary.totalExpense;
          let totalBalance = monthSummary.summary.totalBalance;
          incomeListPast.push(totalIncome);
          expenseListPast.push(totalExpense);
          this.updateSavingsTrendValues(totalIncome, totalExpense, totalBalance);
        } else {
          incomeListFuture.push(monthSummary.summary.totalIncome);
          expenseListFuture.push(monthSummary.summary.totalExpense);
        }
      }
      this.updateSavingsTrendGraph();
      this.updateMonthCharts(incomeListPast, expenseListPast, incomeListFuture, expenseListFuture);
    });
    this.getLastFiveTransactions();
  }

  private updateTotalNetworth() {
    this.summaryService.getAccountSummary().subscribe((accountSummary) => {
      this.netWorth = this.utilsService.formatCurrency(accountSummary.totalBalance);
      this.totalAssets = this.utilsService.formatCurrency(accountSummary.totalIncome);
      this.totalLiabilities = this.utilsService.formatCurrency(accountSummary.totalExpense);
      if (accountSummary.totalIncome == 0 && accountSummary.totalExpense == 0)
        this.noTransactions = true;
    });
  }

  private requestsForLastFiveYears() {
    let startDate = new Date(this.currentYear, 0, 1);
    let dateFrom = this.dateService.removeMonths(startDate, 12);
    let dateTo = this.dateService.removeMonths(startDate, 1);
    const requests = [];
    for (let i = 1; i <= 5; i++) {
      requests.push(this.summaryService.getSummaryByDate(dateFrom, dateTo));
      dateFrom = this.dateService.removeMonths(dateFrom, 12);
      dateTo = this.dateService.removeMonths(dateTo, 12);
    }
    return requests;
  }

  private updateSavingsLastFiveYearsPieChart(yearsWithTransactions: number, totalBalanceFiveYears: number, totalIncomeFiveYears: number, balanceYearsList: number[]) {
    if (yearsWithTransactions > 0) {
      let totalSavingsAverage = totalBalanceFiveYears / yearsWithTransactions;
      this.savingsAverage = this.utilsService.formatCurrency(totalSavingsAverage);
      this.savingsPercentage = this.utilsService.getPercentage(totalBalanceFiveYears, totalIncomeFiveYears);
      this.savingsPercentageFormated = this.utilsService.formatPercentage(this.savingsPercentage);
      let savingsBeforeLastYear = totalBalanceFiveYears - balanceYearsList[0];
      this.savingsTrendPercentage = this.utilsService.percentageChangeFormated(totalSavingsAverage, savingsBeforeLastYear);

      const percentageValue = Math.min(Math.abs(this.savingsPercentage), 100);
      const isNegative = this.savingsPercentage < 0;
      this.savingsDonutChartData = {
        labels: [isNegative ? 'Dívida' : 'Poupança', isNegative ? '' : 'Gastos'],
        datasets: [{
          data: [percentageValue, 100 - percentageValue],
          backgroundColor: [isNegative ? chartPalette.negative : chartPalette.positive, chartPalette.neutral],
          borderColor: [isNegative ? chartPalette.negative : chartPalette.positive, chartPalette.neutral],
          borderWidth: 1
        }]
      };
    } else {
      this.savingsAverage = this.utilsService.formatCurrency(0);
      this.savingsPercentage = 0;
      this.savingsPercentageFormated = this.utilsService.formatPercentage(0);
      this.savingsDonutChartData = {
        datasets: [{ data: [0, 100], backgroundColor: [chartPalette.positive, chartPalette.neutral], borderColor: [chartPalette.positive, chartPalette.neutral], borderWidth: 1 }]
      };
    }
  }

  private updateNetworthTrendChart(balanceLastFiveYearsList: number[]) {
    this.netWorthTrendChange = this.utilsService.formatCurrency(balanceLastFiveYearsList[0]);
    this.netWorthTrendPercentage = this.utilsService.percentageChangeFormated(balanceLastFiveYearsList[0], balanceLastFiveYearsList[1]);
    const labels = [
      this.currentYear - 5,
      this.currentYear - 4,
      this.currentYear - 3,
      this.currentYear - 2,
      this.currentYear - 1
    ];
    this.financialEvolutionChartData = {
      labels,
      datasets: [{
        data: balanceLastFiveYearsList.reverse(),
        label: ' Patrimônio',
        borderColor: chartPalette.positive,
        backgroundColor: chartPalette.fill,
        fill: true,
        tension: 0.35,
        pointRadius: 3,
        pointBackgroundColor: chartPalette.positive
      }]
    };
  }

  private updateSavingsTrendValues(totalIncome: number, totalExpense: number, totalBalance: number) {
    if (totalIncome > 0 && totalExpense == 0) {
      this.values.push(100);
      return;
    }
    if (totalExpense > 0 && totalIncome == 0) {
      this.values.push(-100);
      return;
    }
    if (totalExpense == 0 && totalIncome == 0) {
      this.values.push(0);
      return;
    }
    let balance = Math.round(this.utilsService.getPercentage(totalBalance, totalIncome));
    this.values.push(balance);
  }

  private updateSavingsTrendGraph(): void {
    const labels = this.buildLastTwelveMonthLabels();
    this.savingsTrendChartData = {
      labels,
      datasets: [{
        data: this.values,
        label: ' Poupança',
        borderColor: chartPalette.positive,
        backgroundColor: chartPalette.fill,
        fill: true,
        tension: 0.35,
        pointRadius: 3,
        pointBackgroundColor: chartPalette.positive
      }]
    };
    this.investmentsTrendChartData = {
      labels,
      datasets: [{
        data: this.valuesTemp,
        label: ' Investimentos',
        borderColor: chartPalette.positive,
        backgroundColor: chartPalette.fill,
        fill: true,
        tension: 0.35,
        pointRadius: 3,
        pointBackgroundColor: chartPalette.positive
      }]
    };
  }

  private buildLastTwelveMonthLabels(): string[] {
    const labels: string[] = [];
    for (let i = -11; i <= 0; i++) {
      labels.push(this.dateService.getRelativeMonthName(i));
    }
    return labels;
  }

  private buildRelativeMonthLabels(offsetStart: number, offsetEnd: number): string[] {
    const labels: string[] = [];
    for (let i = offsetStart; i <= offsetEnd; i++) {
      labels.push(this.dateService.getRelativeMonthName(i));
    }
    return labels;
  }

  private updateMonthCharts(incomeListPast: number[], expenseListPast: number[], incomeListFuture: number[], expenseListFuture: number[]) {
    let currentIncome = incomeListFuture[0];
    let currentExpense = expenseListFuture[0];
    this.currentIncomeFormated = this.utilsService.formatCurrency(currentIncome);
    this.currentExpenseFormated = this.utilsService.formatCurrency(currentExpense);

    const pastIncomeValues = incomeListPast.slice(6, 12);
    const pastExpenseValues = expenseListPast.slice(6, 12);
    const futureIncomeValues = incomeListFuture.slice(1, 7);
    const futureExpenseValues = expenseListFuture.slice(1, 7);

    // only last 6 months
    this.pastIncomeChartData = {
      labels: this.buildRelativeMonthLabels(-6, -1),
      datasets: [{
        label: ' Receitas',
        data: pastIncomeValues,
        backgroundColor: this.buildBarGradient(chartPalette.positive),
        borderColor: chartPalette.muted_text_color,
        maxBarThickness: 40,
        borderWidth: {
          top: 2
        },
        borderRadius: 8,
      }]
    };
    this.pastExpensesChartData = {
      labels: this.buildRelativeMonthLabels(-6, -1),
      datasets: [{
        label: ' Despesas',
        data: pastExpenseValues,
        backgroundColor: this.buildBarGradient(chartPalette.negative),
        borderColor: chartPalette.muted_text_color,
        maxBarThickness: 40,
        borderWidth: {
          top: 2
        },
        borderRadius: 8,
      }]
    };
    this.getLastMonth(incomeListPast, currentIncome, expenseListPast, currentExpense);

    this.getNextMonth(incomeListFuture[1], currentIncome, expenseListFuture[1], currentExpense);
    // Removes current month
    incomeListFuture.shift();
    expenseListFuture.shift();
    this.futureIncomeChartData = {
      labels: this.buildRelativeMonthLabels(1, 6),
      datasets: [{
        label: ' Receitas',
        data: futureIncomeValues,
        backgroundColor: this.buildBarGradient(chartPalette.positive),
        borderColor: chartPalette.muted_text_color,
        maxBarThickness: 40,
        borderWidth: {
          top: 2
        },
        borderRadius: 8,
      }]
    };
    this.futureExpensesChartData = {
      labels: this.buildRelativeMonthLabels(1, 6),
      datasets: [{
        label: ' Despesas',
        data: futureExpenseValues,
        backgroundColor: this.buildBarGradient(chartPalette.negative),
        borderColor: chartPalette.muted_text_color,
        maxBarThickness: 40,
        borderWidth: {
          top: 2
        },
        borderRadius: 8,
      }]
    };
  }

  private buildBarGradient(baseColor: string) {
    return (context: any) => {
      const { chart } = context;
      const { ctx, chartArea } = chart;
      if (!chartArea) return baseColor;

      const gradient = ctx.createLinearGradient(0, chartArea.bottom, 0, chartArea.top);
      gradient.addColorStop(0, `${baseColor}55`);
      gradient.addColorStop(1, baseColor);
      return gradient;
    };
  }

  private getNextMonth(incomeNextMonth: number, currentIncome: number, expenseNextMonth: number, currentExpense: number) {
    this.nextMonthIncomeValue = this.utilsService.formatCurrency(incomeNextMonth);
    this.nextMonthIncomePercentage = this.utilsService.percentageChangeFormated(incomeNextMonth, currentIncome);
    this.nextMonthExpenseValue = this.utilsService.formatCurrency(expenseNextMonth);
    this.nextMonthExpensePercentage = this.utilsService.percentageChangeFormated(expenseNextMonth, currentExpense);
  }

  private getLastMonth(incomeListPast: number[], currentIncome: number, expenseListPast: number[], currentExpense: number) {
    this.lastMonthIncomeValue = this.utilsService.formatCurrency(incomeListPast[11]);
    this.lastMonthIncomePercentage = this.utilsService.percentageChangeFormated(currentIncome, incomeListPast[11]);

    this.lastMonthExpenseValue = this.utilsService.formatCurrency(expenseListPast[11]);
    this.lastMonthExpensePercentage = this.utilsService.percentageChangeFormated(currentExpense, expenseListPast[11]);

    let balanceLastMonth = incomeListPast[11] - expenseListPast[11];
    this.balanceLastMonth = this.utilsService.formatCurrency(balanceLastMonth);
    this.balanceLastMonthPercentage = this.utilsService.percentageChangeFormated(balanceLastMonth, incomeListPast[10] - expenseListPast[10]);
  }

  private getLastFiveTransactions() {
    let size = 5;
    let results: any[] = [];
    let transactions: Transaction[] = [];
    this.summaryService.getRecentTransactions().subscribe(
      result => {
        result.income.forEach((income: Transaction) => {
          income.type = 'Receita';
        });
        transactions.push(...result.income);

        result.expense.forEach((expense: Transaction) => {
          expense.type = 'Despesa';
          expense.value = expense.value * -1;
          expense.category = getCategoryByNameInEnglish(expense.category.toString());
        });
        transactions.push(...result.expense);

        transactions.sort((a: Transaction, b: Transaction) =>
          new Date(b.date).getTime() - new Date(a.date).getTime());
        results = transactions.slice(0, size);
        this.recentTransactions.push(...results);
      });
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

  getChangeColorDonut(change: number): string {
    if (change < 0) return 'negative';
    if (change > 0) return 'positive';
    return 'neutral';
  }

  getRelativeMonthName(n: number) : string {
    return this.dateService.getRelativeMonthName(n);
  }
}
