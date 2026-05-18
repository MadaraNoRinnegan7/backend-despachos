package com.innovatech.despachos.repository;

import com.innovatech.despachos.model.Despacho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DespachoRepository extends JpaRepository<Despacho, Long> {
    // Spring Data JPA genera automáticamente la consulta SQL basada en el nombre del método
    List<Despacho> findByEstado(Despacho.EstadoDespacho estado);
}
