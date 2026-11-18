package tn.esprit.autoloc.service;

public interface IGenericWriteService<T, K> {
    T create(T entity);

    T update(K id, T entity);

    void deleteById(K id);
}
