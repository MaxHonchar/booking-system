package com.test.booking.domain;

import com.test.booking.enums.AccommodationType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

@Getter
@Setter
@Entity
@Table(name = "unit_properties")
public class UnitProperties {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "unit_properties_seq")
    @SequenceGenerator(name = "unit_properties_seq", sequenceName = "unit_properties_seq_id")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @Column
    @Enumerated(EnumType.STRING)
    @JdbcType(PostgreSQLEnumJdbcType.class)
    private AccommodationType type = AccommodationType.FLAT;

    private int rooms;
    private int floor;

}
