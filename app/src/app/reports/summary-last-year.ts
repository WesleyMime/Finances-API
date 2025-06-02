export interface SummaryLastYear {
    avgBalanceYear: number;
    percentageSavingsRate: string;
    finalBalanceEachMonth: number[];
    income: {
        description: string;
        value: number;
        date: Date;
    }[];
    expenses: {
        description: string;
        value: number;
        date: Date;
        category: string;
    }[];
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
