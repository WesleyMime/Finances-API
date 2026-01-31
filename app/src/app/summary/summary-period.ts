import { SummaryByDate } from "./summary-by-date";

export interface SummaryPeriod {

  totalIncomePeriod: number,
  totalExpensePeriod: number,
  totalBalancePeriod: number,
  summaryList: SummaryByDate[]
}
