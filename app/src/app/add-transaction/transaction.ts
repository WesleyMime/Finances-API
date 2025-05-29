export interface Transaction {
  type: string;
  category: string;
  value: number | null;
  date: string;
  description: string;

}
