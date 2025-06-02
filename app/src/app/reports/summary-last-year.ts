export interface SummaryLastYear {
    totalIncome: number;
    totalExpense: number;
    avgBalanceYear: number;
    percentageSavingsRate: string;
    finalBalanceEachMonth: number[];
    income: Income[];
    expenses: Expense[];
    // TODO
    netWorthTotal: string;
    netWorthChange: string;
    netWorthYears: string[];
    savingsRateChange: string;
    incomeExpenseChange: string;
    incomeExpensePercentage: string;
    incomeExpenseMonths: string[];
    spendingTotal: string;
    spendingChange: string;
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