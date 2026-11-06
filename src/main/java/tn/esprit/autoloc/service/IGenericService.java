package tn.esprit.autoloc.service;

public interface IGenericService<T, ID> extends
        IGenericReadService<T, ID>,
        IGenericWriteService<T, ID>,
        IGenericValidationService<T> {
}
