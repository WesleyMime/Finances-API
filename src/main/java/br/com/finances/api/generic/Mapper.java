package br.com.finances.api.generic;

public interface Mapper<S, T>{

	public T map(S source);
}
