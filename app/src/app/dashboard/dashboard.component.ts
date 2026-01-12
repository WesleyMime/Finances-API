import { CurrencyPipe, NgClass, NgFor } from '@angular/common';
import { HeaderComponent } from "../header/header.component";
import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';


interface Transaction {
  date: string;
  category: string;
  description: string;
  amount: number;
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
    // Assuming rawSvgContent contains your SVG string
    this.sanitizer = sanitizer;
  }
  // Mock Data (replace with actual data fetched from a service)
  netWorth: number = 125450;
  totalAssets: number = 150000;
  totalLiabilities: number = 24550;

  incomeExpenseChange: number = 2500;
  incomeExpensePercentage: number = 15;

  netWorthTrendChange: number = 10000;
  netWorthTrendPercentage: number = 10;

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
    { date: '2024-07-15', category: 'Groceries', description: 'Supermarket purchase', amount: -85.50 },
    { date: '2024-07-14', category: 'Salary', description: 'Monthly paycheck', amount: 5000.00 },
    { date: '2024-07-12', category: 'Rent', description: 'Apartment rent', amount: -1500.00 },
    { date: '2024-07-10', category: 'Dining', description: 'Restaurant dinner', amount: -60.00 },
    { date: '2024-07-08', category: 'Utilities', description: 'Electricity bill', amount: -120.00 },
  ];

  getAmountClass(amount: number): string {
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

  ngOnInit(): void {
    this.updateGraph();
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

    // Title
//     svg += `<text x="${this.graphWidth / 2}" y="30" class="title" text-anchor="middle">Monthly Sales Data</text>`;

    // Vertical axis
    svg += `<line x1="${this.padding.left}" y1="${this.padding.top}" x2="${this.padding.left}" y2="${this.graphHeight - this.padding.bottom }" class="grid-line" stroke-width="2"></line>`;

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
