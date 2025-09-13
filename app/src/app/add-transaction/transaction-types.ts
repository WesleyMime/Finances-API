export const transactionTypeEnum: string[] = ['Receita', 'Despesa'];

export function isExpense(type: string) {
  return type == transactionTypeEnum[1];
}

export function isIncome(type: string) {
  return type == transactionTypeEnum[0];
}
