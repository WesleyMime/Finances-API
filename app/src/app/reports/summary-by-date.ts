export interface SummaryByDate {
    totalIncome: number;
    totalExpense: number;
    finalBalance: number;
    totalExpenseByCategory: {
        category: string;
        totalValue: number;
    }[];
}
