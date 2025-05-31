export interface Report {
    finalBalance: number;
    totalExpense: number;
    totalIncome: number;
    totalExpenseByCategory: [
        {
            category: string;
            totalValue: number;
        }
    ]
}
