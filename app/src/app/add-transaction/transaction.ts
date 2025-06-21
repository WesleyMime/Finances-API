export interface Transaction {
  id?: number | null;
  type: string;
  category: string;
  value: number;
  date: string;
  description: string;
}

export const emptyTransaction: Transaction = {
  id: null,
  type: '',
  category: '',
  value: 0,
  date: '',
  description: ''
}
