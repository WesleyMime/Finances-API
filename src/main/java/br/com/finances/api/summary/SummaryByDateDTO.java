package br.com.finances.api.summary;

import java.io.Serializable;

public record SummaryByDateDTO(String date, SummaryBasicDTO summary) implements Serializable {
}
