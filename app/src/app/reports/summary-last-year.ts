export interface SummaryLastYear {
    totalYearIncome: number;
    totalYearExpense: number;
    avgBalanceYear: number;
    percentageSavingsRate: string;
    finalBalanceEachMonth: number[];
    income: Income[];
    expenses: Expense[];
}

export interface Income {
    description: string;
    value: number;
    date: Date;
}

export interface Expense {
    description: string;
    value: number;
    date: Date;
    category: string;
}