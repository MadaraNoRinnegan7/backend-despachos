package com.innovatech.despachos.service;

import com.innovatech.despachos.model.Despacho;
import com.innovatech.despachos.repository.DespachoRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class DespachoService {

    private final DespachoRepository repository;

    public DespachoService(DespachoRepository repository) {
        this.repository = repository;
    }

    public List<Despacho> findAll() {
        return repository.findAll();
    }

    public Optional<Despacho> findById(Long id) {
        return repository.findById(id);
    }

    public Despacho save(Despacho despacho) {
        return repository.save(despacho);
    }

    public Optional<Despacho> update(Long id, Despacho datos) {
        return repository.findById(id).map(existing -> {
            if (datos.getCliente() != null)    existing.setCliente(datos.getCliente());
            if (datos.getProducto() != null)   existing.setProducto(datos.getProducto());
            if (datos.getDireccion() != null)  existing.setDireccion(datos.getDireccion());
            if (datos.getEstado() != null)     existing.setEstado(datos.getEstado());
            return repository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}
