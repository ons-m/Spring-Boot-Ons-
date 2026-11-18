package tn.esprit.autoloc.service;

public interface IGenericService<T, K> extends
        IGenericReadService<T, K>,
        IGenericWriteService<T, K>,
        IGenericValidationService<T> {
}
