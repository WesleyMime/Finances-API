export interface Transaction {
  id?: number | null;
  type: string;
  category: string;
  value: number;
  date: string;
  description: string;

}
