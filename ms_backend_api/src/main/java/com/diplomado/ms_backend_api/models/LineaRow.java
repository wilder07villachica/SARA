package com.diplomado.ms_backend_api.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "linea_diagnostico_consolidado")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LineaRow {

    @Id
    @Column(name = "numeroTelefono")
    private String numeroTelefono;
}
