import { Category } from "../category";

export interface Transaction {
  id: number;
  type: string;
  category: Category;
  value: number;
  date: string;
  description: string;
  selected: boolean;
}

export interface TransactionForm {
  id?: number | null;
  type: string;
  category: string;
  value: number;
  date: string;
  description: string;
}

export const emptyTransaction: TransactionForm = {
  type: '',
  category: '',
  value: 0,
  date: '',
  description: ''
}
