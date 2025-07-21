package br.com.finances.api.generic;

import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;
import java.util.function.BiFunction;

public interface GenericService<T extends GenericModel, S extends GenericDTO, U extends GenericForm> {

    List<S> getAll(String description, Principal principal);
    ResponseEntity<S> getOne(String id);
    ResponseEntity<List<S>> getByDate(String yearString, String monthString);

    ResponseEntity<S> post(U form, Principal principal);

    ResponseEntity<List<S>> postList(List<U> forms, Principal principal);

    ResponseEntity<S> put(String id, U form, BiFunction<T, U, T> function, Principal principal);

    ResponseEntity<S> delete(String id, Principal principal);
}
