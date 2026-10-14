package tn.esprit.autoloc.service;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.autoloc.exception.ResourceNotFoundException;

import java.util.List;

public abstract class GenericServiceImpl<T, ID> implements IGenericService<T, ID> {

    private final JpaRepository<T, ID> repository;

    protected GenericServiceImpl(JpaRepository<T, ID> repository) {
        this.repository = repository;
    }

    protected abstract String getEntityName();

    protected abstract ID getId(T entity);

    protected abstract void setFields(T source, T target);

    @Override
    public T create(T entity) {
        if (entity != null && getId(entity) != null) {
            throw new IllegalArgumentException("Une nouvelle entite ne doit pas avoir d'identifiant");
        }
        if (!check(entity, true)) {
            throw new IllegalArgumentException("Les donnees de " + getEntityName() + " sont invalides");
        }

        return repository.save(entity);
    }

    @Override
    public T findById(ID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(getEntityName(), String.valueOf(id)));
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public T update(ID id, T entity) {
        T existingEntity = findById(id);

        if (!check(entity, false)) {
            throw new IllegalArgumentException("Les donnees de " + getEntityName() + " sont invalides");
        }

        setFields(entity, existingEntity);
        return repository.save(existingEntity);
    }

    @Override
    public void deleteById(ID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException(getEntityName(), String.valueOf(id));
        }

        repository.deleteById(id);
    }
}
