import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class UtilsService {  
  getChangeColorGood(change: string): string {
    if (change.startsWith('+')) {
      return 'green';
    } else if (change.startsWith('-')) {
      return 'red';
    }
    return 'gray';
  }

  getChangeColorBad(change: string): string {
    if (change.startsWith('+')) {
      return 'red';
    } else if (change.startsWith('-')) {
      return 'green';
    }
    return 'gray';
  }

  formatCurrency(value: number): string {
    return value.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  getPercentageChange(num1: number, num2: number): number {
    let difference = num1 - num2;
    return difference * 100 / num2;
  }

  formatPercentage(diffPercent: number): string {
    if (!isFinite(diffPercent)) return '0%';
    diffPercent = Math.round(diffPercent * 100) / 100;
    return diffPercent > 0 ? `+${diffPercent}%` : `${diffPercent}%`;
  }
}