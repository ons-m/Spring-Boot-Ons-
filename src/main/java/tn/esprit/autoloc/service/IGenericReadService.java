package tn.esprit.autoloc.service;

import java.util.List;

public interface IGenericReadService<T, ID> {
    T findById(ID id);

    List<T> findAll();
}
