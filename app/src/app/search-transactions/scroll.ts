import { Transaction } from "../add-transaction/transaction";

export interface Scroll {
  data: Transaction[],
  hasNext: boolean,
  lastId: number,
  lastDate: string
}
