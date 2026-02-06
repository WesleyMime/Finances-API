import { inject, Injectable, OnInit } from '@angular/core';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';
import { DateService } from './date.service';

@Injectable({
  providedIn: 'root',
})
export class DrawGraphService {
  dateService = inject(DateService);
  sanitizer = inject(DomSanitizer);

  currentMonth = this.dateService.currentDate.getMonth();
  padding = { top: 25, right: 25, bottom: 25, left: 25 };

  draw(values: number[], graphWidth: number, graphHeight: number): SafeHtml {    
    let chartWidth = graphWidth - this.padding.left - this.padding.right;
    let chartHeight = graphHeight - this.padding.top - this.padding.bottom;
    let svg = '';

    const points = values.map((val, i) => ({
      x: this.padding.left + (i * (chartWidth / 11)),
      y: this.padding.top + chartHeight / 2 - (val / 100 * chartHeight / 2)
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
              <text x="${point.x}" y="${graphHeight - this.padding.bottom + 15}" class="label" text-anchor="middle">
                ${this.dateService.getRelativeMonthName(this.currentMonth + i - 1)}
              </text>
              <text x="${point.x}" y="${point.y - 10}" class="label" font-weight="bold" text-anchor="middle">
                ${values[i]}
              </text>`;
    });
    return this.sanitizer.bypassSecurityTrustHtml(svg);
  }  
}
