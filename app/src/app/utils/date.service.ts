import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class DateService {
  currentDate = new Date();

  months = ['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'];

  addMonths(date: Date, months: number): Date {
    let newDate = new Date(date);
    newDate.setMonth(date.getMonth() + months);
    newDate.setDate(1);
    return newDate;
  }

  removeMonths(date: Date, months: number): Date {
    return this.addMonths(date, months * -1);
  }

  getRelativeMonthName(n : number) : string {
    // Return n months after or before current one
    let currentMonth = this.currentDate.getMonth() + 12;
    let month = this.months[(currentMonth + n) % 12];
    return month;
  }  
}
