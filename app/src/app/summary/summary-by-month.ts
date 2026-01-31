export interface SummaryByMonth {
    totalIncome: number;
    totalExpense: number;
    finalBalance: number;
    totalExpenseByCategory: {
        category: string;
        totalValue: number;
    }[];
}
