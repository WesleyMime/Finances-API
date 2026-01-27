package br.com.finances.api.summary;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public record SummaryPeriodDTO(BigDecimal totalIncomePeriod,
							   BigDecimal totalExpensePeriod,
							   BigDecimal totalBalancePeriod,
							   List<SummaryByDateDTO> summaryList) implements Serializable {
}
