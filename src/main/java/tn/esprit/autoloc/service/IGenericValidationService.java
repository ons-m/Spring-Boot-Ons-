package tn.esprit.autoloc.service;

public interface IGenericValidationService<T> {
    boolean check(T entity, boolean isNew);
}
