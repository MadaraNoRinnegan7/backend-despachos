package com.innovatech.despachos.controller;

import com.innovatech.despachos.model.Despacho;
import com.innovatech.despachos.service.DespachoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/despachos")
@CrossOrigin(origins = "*")   // Permite llamadas desde el frontend (CORS)
@Tag(name = "Despachos", description = "API de gestión de despachos - Innovatech Chile")
public class DespachoController {

    private final DespachoService service;

    public DespachoController(DespachoService service) {
        this.service = service;
    }

    // GET /despachos → Listar todos los despachos
    @GetMapping
    @Operation(summary = "Listar todos los despachos")
    public List<Despacho> getAll() {
        return service.findAll();
    }

    // GET /despachos/{id} → Obtener un despacho por ID
    @GetMapping("/{id}")
    @Operation(summary = "Obtener despacho por ID")
    public ResponseEntity<Despacho> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /despachos → Crear nuevo despacho
    @PostMapping
    @Operation(summary = "Crear nuevo despacho")
    public ResponseEntity<Despacho> create(@RequestBody Despacho despacho) {
        Despacho saved = service.save(despacho);
        return ResponseEntity.ok(saved);
    }

    // PUT /despachos/{id} → Actualizar despacho (estado, dirección, etc.)
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar despacho")
    public ResponseEntity<Despacho> update(@PathVariable Long id, @RequestBody Despacho datos) {
        return service.update(id, datos)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE /despachos/{id} → Eliminar despacho
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar despacho")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return service.delete(id)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    // GET /despachos/health → Healthcheck del API
    @GetMapping("/health")
    @Operation(summary = "Verificar estado del servicio")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Backend Despachos OK - Innovatech Chile");
    }
}
