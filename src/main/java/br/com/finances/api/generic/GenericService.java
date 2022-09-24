package br.com.finances.api.generic;

import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.BiFunction;

public interface GenericService<T extends GenericModel, S extends GenericDTO, U extends GenericForm> {

    ResponseEntity<List<S>> getAll(String description);
    ResponseEntity<S> getOne(String id);
    ResponseEntity<List<S>> getByDate(String yearString, String monthString);
    ResponseEntity<S> post(U form);
    ResponseEntity<S> put(String id, U form, BiFunction<T, U, T> function);
    ResponseEntity<S> delete(String id);
}
