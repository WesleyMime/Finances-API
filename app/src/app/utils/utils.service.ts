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
  
  getPercentage(num1: number, num2: number): number {
    return num1 * 100 / num2;
  }

  getPercentageChange(num1: number, num2: number): number {
    let difference = num1 - num2;
    return difference * 100 / num2;
  }

  percentageFormated(num1: number, num2: number): string {
    let percentage = this.getPercentage(num1, num2);
    return this.formatPercentage(percentage);
  }

  percentageChangeFormated(num1: number, num2: number): string {
    let change = this.getPercentageChange(num1, num2);
    return this.formatPercentage(change);
  }

  formatPercentage(percentage: number): string {
    if (!isFinite(percentage)) return '0%';
    percentage = Math.round(percentage * 100) / 100;
    return percentage > 0 ? `+${percentage}%` : `${percentage}%`;
  }
}